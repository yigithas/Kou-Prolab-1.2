package io;

import engine.Grid;
import model.common.RobotStatus;
import model.robot.Robot;

import java.util.Comparator;
import java.util.List;

public class ConsoleOutputFormatter {

    public static String formatReport(Grid grid) {
        if (grid == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        List<Robot> robots = grid.getRobots();

        robots.stream()
                .sorted(Comparator.comparingInt(Robot::getId))
                .forEach(r -> {
                    sb.append(r.getReportLine()).append("\n");
                });

        int deliveredCount = 0;
        int totalRemainingEnergy = 0;

        for (Robot r : robots) {
            if (r.getStatus() == RobotStatus.TESLIM) {
                deliveredCount++;
            }
            totalRemainingEnergy += r.getEnergy();
        }

        sb.append("TESLIM ").append(deliveredCount).append("\n");
        sb.append("KALAN_ENERJI ").append(totalRemainingEnergy);

        return sb.toString();
    }

    public static void printReport(Grid grid) {
        System.out.println(formatReport(grid));
    }
}
