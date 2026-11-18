import command.Command;
import engine.Grid;
import engine.SimulationManager;
import io.CommandFileReader;
import io.ConsoleOutputFormatter;
import io.InvalidCommandException;
import io.InvalidMapException;
import io.MapFileReader;
import ui.MainFrame;

import javax.swing.SwingUtilities;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        File mapFile = null;
        File cmdFile = null;

        if (args.length >= 2) {
            mapFile = new File(args[0]);
            cmdFile = new File(args[1]);
        } else {
            File[] candidateMaps = new File[] {
                new File("harita.txt"),
                new File("src/harita.txt"),
                new File(System.getProperty("user.dir"), "harita.txt")
            };
            for (File f : candidateMaps) {
                if (f.exists()) {
                    mapFile = f;
                    break;
                }
            }

            File[] candidateCmds = new File[] {
                new File("komutlar.txt"),
                new File("src/komutlar.txt"),
                new File(System.getProperty("user.dir"), "komutlar.txt")
            };
            for (File f : candidateCmds) {
                if (f.exists()) {
                    cmdFile = f;
                    break;
                }
            }
        }

        if (mapFile != null && cmdFile != null && mapFile.exists() && cmdFile.exists()) {
            try {
                MapFileReader mapReader = new MapFileReader();
                MapFileReader.ParseResult mapResult = mapReader.read(mapFile);
                Grid grid = mapResult.getGrid();

                CommandFileReader cmdReader = new CommandFileReader();
                List<Command> commands = cmdReader.read(cmdFile);

                SimulationManager simulationManager = new SimulationManager(grid, commands);
                simulationManager.runAll();

                ConsoleOutputFormatter.printReport(grid);

            } catch (InvalidMapException | InvalidCommandException e) {
                System.err.println(e.getMessage()+"\n");
                if (args.length >= 2) System.exit(1);
            } catch (IOException e) {
                System.err.println(e.getMessage()+"\n");
                if (args.length >= 2) System.exit(1);
            }
        }

        if (args.length >= 2) {
            return;
        }

        final File finalMap = mapFile;
        final File finalCmd = cmdFile;
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            if (finalMap != null && finalCmd != null && finalMap.exists() && finalCmd.exists()) {
                frame.loadSimulation(finalMap, finalCmd);
            }
            frame.setVisible(true);
        });
    }
}
