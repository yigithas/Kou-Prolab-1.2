package io;

import engine.Grid;
import model.common.Position;
import model.ground.*;
import model.robot.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MapFileReader {

    public static class ParseResult {
        private final Grid grid;
        private final int initialEnergy;

        public ParseResult(Grid grid, int initialEnergy) {
            this.grid = grid;
            this.initialEnergy = initialEnergy;
        }

        public Grid getGrid() {
            return grid;
        }

        public int getInitialEnergy() {
            return initialEnergy;
        }
    }

    public ParseResult read(File file) throws IOException, InvalidMapException {
        if (file == null || !file.exists()) {
            throw new IOException("Harita dosyası bulunamadı: " + (file != null ? file.getPath() : "null"));
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return parse(reader);
        }
    }

    public ParseResult parse(BufferedReader reader) throws IOException, InvalidMapException {
        int width = -1;
        int height = -1;
        int initialEnergy = -1;

        Map<Integer, String> robotBodies = new TreeMap<>();
        Map<Integer, Position> robotPositions = new HashMap<>();

        boolean readingMap = false;
        List<String> mapLines = new ArrayList<>();
        int mapStartLineNum = -1;

        String rawLine;
        int lineNumber = 0;

        while ((rawLine = reader.readLine()) != null) {
            lineNumber++;
            String line = rawLine.trim();

            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            if (!readingMap) {
                if (line.startsWith("BOYUT")) {
                    String[] tokens = line.split("\\s+");
                    if (tokens.length != 3) {
                        throw new InvalidMapException(lineNumber, "BOYUT formatı hatalı. Beklenen: BOYUT g y");
                    }
                    try {
                        width = Integer.parseInt(tokens[1]);
                        height = Integer.parseInt(tokens[2]);
                    } catch (NumberFormatException e) {
                        throw new InvalidMapException(lineNumber, "Genişlik veya yükseklik sayısal olmalıdır: " + line);
                    }
                    if (width <= 0 || height <= 0) {
                        throw new InvalidMapException(lineNumber, "Harita boyutları 0'dan büyük olmalıdır: " + width + "x" + height);
                    }
                } else if (line.startsWith("ENERJI")) {
                    String[] tokens = line.split("\\s+");
                    if (tokens.length != 2) {
                        throw new InvalidMapException(lineNumber, "ENERJI formatı hatalı. Beklenen: ENERJI e");
                    }
                    try {
                        initialEnergy = Integer.parseInt(tokens[1]);
                    } catch (NumberFormatException e) {
                        throw new InvalidMapException(lineNumber, "Enerji sayısal olmalıdır: " + tokens[1]);
                    }
                    if (initialEnergy < 0) {
                        throw new InvalidMapException(lineNumber, "Enerji negatif olamaz: " + initialEnergy);
                    }
                } else if (line.startsWith("ROBOT")) {
                    String[] tokens = line.split("\\s+");
                    if (tokens.length != 3) {
                        throw new InvalidMapException(lineNumber, "ROBOT formatı hatalı. Beklenen: ROBOT n GOVDE");
                    }
                    int robotId;
                    try {
                        robotId = Integer.parseInt(tokens[1]);
                    } catch (NumberFormatException e) {
                        throw new InvalidMapException(lineNumber, "Robot numarası sayı olmalıdır: " + tokens[1]);
                    }
                    if (robotId < 1 || robotId > 9) {
                        throw new InvalidMapException(lineNumber, "Robot numarası 1 ile 9 arasında olmalıdır: " + robotId);
                    }
                    if (robotBodies.containsKey(robotId)) {
                        throw new InvalidMapException(lineNumber, "Mükerrer robot ID tanımı: " + robotId);
                    }

                    String bodyType = tokens[2].toUpperCase(Locale.ENGLISH).replace('İ', 'I');
                    if (!isValidBodyType(bodyType)) {
                        throw new InvalidMapException(lineNumber, "Bilinmeyen robot gövdesi: " + tokens[2]);
                    }
                    robotBodies.put(robotId, bodyType);
                } else if (line.equals("HARITA")) {
                    if (width <= 0 || height <= 0) {
                        throw new InvalidMapException(lineNumber, "HARITA satırından önce BOYUT belirtilmelidir.");
                    }
                    if (initialEnergy < 0) {
                        throw new InvalidMapException(lineNumber, "HARITA satırından önce ENERJI belirtilmelidir.");
                    }
                    if (robotBodies.isEmpty()) {
                        throw new InvalidMapException(lineNumber, "HARITA satırından önce en az bir ROBOT tanımlanmalıdır.");
                    }
                    readingMap = true;
                    mapStartLineNum = lineNumber;
                } else {
                    throw new InvalidMapException(lineNumber, "Geçersiz başlık komutu: " + line);
                }
            } else {
                if (line.length() != width) {
                    throw new InvalidMapException(lineNumber, "Harita satırı genişliği " + width + " karakter olmalı, ancak " + line.length() + " karakter: '" + line + "'");
                }
                mapLines.add(line);
                if (mapLines.size() == height) {
                    break;
                }
            }
        }

        if (!readingMap) {
            throw new InvalidMapException(lineNumber, "Dosyada 'HARITA' bölümü bulunamadı.");
        }
        if (mapLines.size() != height) {
            throw new InvalidMapException(mapStartLineNum, "Harita yüksekliği " + height + " satır olmalı, ancak dosyada " + mapLines.size() + " satır bulundu.");
        }

        Grid grid = new Grid(width, height);

        for (int y = 0; y < height; y++) {
            String mapRow = mapLines.get(y);
            for (int x = 0; x < width; x++) {
                char ch = mapRow.charAt(x);

                if (ch >= '1' && ch <= '9') {
                    int rId = ch - '0';
                    if (robotPositions.containsKey(rId)) {
                        throw new InvalidMapException(mapStartLineNum + 1 + y, "Haritada ikinci bir '" + rId + "' robot başlangıç hücresi bulundu!");
                    }
                    if (!robotBodies.containsKey(rId)) {
                        throw new InvalidMapException(mapStartLineNum + 1 + y, "Haritada '" + rId + "' hücresi var fakat dosya başında ROBOT " + rId + " tanımı yapılmamış!");
                    }
                    robotPositions.put(rId, new Position(x, y));
                    grid.setGround(x, y, new PlainGround());
                } else {
                    Ground ground = createGroundBySymbol(ch, mapStartLineNum + 1 + y);
                    grid.setGround(x, y, ground);
                }
            }
        }

        for (Integer rId : robotBodies.keySet()) {
            if (!robotPositions.containsKey(rId)) {
                throw new InvalidMapException(mapStartLineNum, "ROBOT " + rId + " tanımlanmış fakat harita içinde '" + rId + "' hücresi bulunamadı!");
            }
        }

        for (Map.Entry<Integer, String> entry : robotBodies.entrySet()) {
            int rId = entry.getKey();
            String body = entry.getValue();
            Position startPos = robotPositions.get(rId);

            Robot robot = createRobotInstance(rId, body, startPos, initialEnergy);
            grid.addRobot(robot);
        }

        return new ParseResult(grid, initialEnergy);
    }

    private boolean isValidBodyType(String body) {
        return body.equals("PALETLI") || body.equals("SICRAYAN") ||
               body.equals("HAVALI") || body.equals("KIZAKLI") ||
               body.equals("ZIRHLI");
    }

    private Ground createGroundBySymbol(char ch, int lineNum) throws InvalidMapException {
        switch (ch) {
            case '.': return new PlainGround();
            case 'T': return new DustStorm();
            case 'K': return new CraterRock();
            case '~': return new MethaneLake();
            case 'I': return new IceCrust();
            case 'X': return new Obstacle();
            case 'H': return new DeliveryRamp();
            case 'C': return new MuddyPlace();
            default:
                throw new InvalidMapException(lineNum, "Harita dışı geçersiz zemin karakteri: '" + ch + "'");
        }
    }

    private Robot createRobotInstance(int id, String body, Position pos, int energy) {
        switch (body) {
            case "PALETLI":  return new TrackedRobot(id, pos, energy);
            case "SICRAYAN": return new JumpingRobot(id, pos, energy);
            case "HAVALI":   return new FlyingRobot(id, pos, energy);
            case "KIZAKLI":  return new SlidingRobot(id, pos, energy);
            case "ZIRHLI":   return new ShieldedRobot(id, pos, energy);
            default:         return new TrackedRobot(id, pos, energy);
        }
    }
}
