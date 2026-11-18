package io;

import command.Command;
import command.ForwardCommand;
import command.LeftCommand;
import command.RigthCommand;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandFileReader {

    public List<Command> read(File file) throws IOException, InvalidCommandException {
        if (file == null || !file.exists()) {
            throw new IOException("Komut dosyası bulunamadı: " + (file != null ? file.getPath() : "null"));
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return parse(reader);
        }
    }

    public List<Command> parse(BufferedReader reader) throws IOException, InvalidCommandException {
        List<Command> commandList = new ArrayList<>();
        String rawLine;
        int lineNumber = 0;

        while ((rawLine = reader.readLine()) != null) {
            lineNumber++;

            String line = rawLine;
            int commentIndex = line.indexOf('#');
            if (commentIndex != -1) {
                line = line.substring(0, commentIndex);
            }
            line = line.trim();

            if (line.isEmpty()) {
                continue;
            }

            String[] tokens = line.split("\\s+");
            String cmdName = tokens[0].toUpperCase();

            if (cmdName.equals("ILERI")) {
                if (tokens.length != 2) {
                    throw new InvalidCommandException(lineNumber, "ILERI komutu adım sayısı parametresi gerektirir. Örn: ILERI 6");
                }
                int steps;
                try {
                    steps = Integer.parseInt(tokens[1]);
                } catch (NumberFormatException e) {
                    throw new InvalidCommandException(lineNumber, "ILERI parametresi tam sayı olmalıdır: " + tokens[1]);
                }
                if (steps < 1 || steps > 99) {
                    throw new InvalidCommandException(lineNumber, "Adım sayısı 1 ile 99 arasında olmalıdır: " + steps);
                }
                commandList.add(new ForwardCommand(steps));

            } else if (cmdName.equals("SAG")) {
                if (tokens.length != 1) {
                    throw new InvalidCommandException(lineNumber, "SAG komutu parametre almaz: " + line);
                }
                commandList.add(new RigthCommand());

            } else if (cmdName.equals("SOL")) {
                if (tokens.length != 1) {
                    throw new InvalidCommandException(lineNumber, "SOL komutu parametre almaz: " + line);
                }
                commandList.add(new LeftCommand());

            } else {
                throw new InvalidCommandException(lineNumber, "Bilinmeyen komut: '" + tokens[0] + "'. Yalnızca ILERI n, SAG veya SOL geçerlidir.");
            }
        }

        return commandList;
    }
}
