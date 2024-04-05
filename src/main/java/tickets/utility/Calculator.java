package tickets.utility;

import java.time.LocalDateTime;

import java.time.temporal.ChronoUnit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class Calculator {
    public static Map<String, Long> calculateMinimumFlightTime(List<Map<String, Object>> fileParsedToList) {
        Map<String, Long> carriersWithMinimumMinutesFly = new HashMap<>();

        for (Map<String, Object> ticket : fileParsedToList) {
            carriersWithMinimumMinutesFly.put(String.valueOf(ticket.get("carrier")), null);
        }

        for (Map.Entry<String, Long> carrierInfo : carriersWithMinimumMinutesFly.entrySet()) {
            String carrierName = carrierInfo.getKey();
            Long carrierMinimalMinutesFlight = carrierInfo.getValue();

            for (Map<String, Object> ticket : fileParsedToList) {
                String ticketCarrier = String.valueOf(ticket.get("carrier"));

                if (ticketCarrier.equals(carrierName)) {
                    LocalDateTime[] departureDateTime = getLocalDateTimesFromTicket(ticket);
                    Long ticketFlightTimeInMinutes = ChronoUnit.MINUTES.between(departureDateTime[0],
                            departureDateTime[1]);

                    if (carrierMinimalMinutesFlight == null || ticketFlightTimeInMinutes < carrierMinimalMinutesFlight) {
                        carriersWithMinimumMinutesFly.put(carrierName, ticketFlightTimeInMinutes);
                    }
                }
            }
        }
        return carriersWithMinimumMinutesFly;
    }

    public static Double calculateDifferenceBetweenAveragePriceMedian(List<Map<String, Object>> fileParsedToList) {
        int[] ticketsPrices = new int[fileParsedToList.size()];
        int i = 0;

        for (Map<String, Object> map : fileParsedToList) {
            ticketsPrices[i++] = (int) map.get("price");
        }
        Double averagePrice = calculateAveragePrice(ticketsPrices);
        Double medianPrice = calculateMedianPrice(ticketsPrices);

        return averagePrice - medianPrice;
    }

    private static LocalDateTime[] getLocalDateTimesFromTicket(Map<String, Object> ticket) {
        String[] departureDatesArr;
        String[] arrivalDatesArr;
        String[] departureTimesArr;
        String[] arrivalTimesArr;
        LocalDateTime[] resultsArr = new LocalDateTime[2];

        departureDatesArr = ticket.get("departure_date").toString().split("\\.");
        arrivalDatesArr = ticket.get("arrival_date").toString().split("\\.");
        departureTimesArr = ticket.get("departure_time").toString().split(":");
        arrivalTimesArr = ticket.get("arrival_time").toString().split(":");

        resultsArr[0] = LocalDateTime.of(Integer.parseInt(
                "20" + departureDatesArr[2]),
                Integer.parseInt(departureDatesArr[1]),
                Integer.parseInt(departureDatesArr[0]),
                Integer.parseInt(departureTimesArr[0]),
                Integer.parseInt(departureTimesArr[1])
        );
        resultsArr[1] = LocalDateTime.of(
                Integer.parseInt("20" + arrivalDatesArr[2]),
                Integer.parseInt(arrivalDatesArr[1]),
                Integer.parseInt(arrivalDatesArr[0]),
                Integer.parseInt(arrivalTimesArr[0]),
                Integer.parseInt(arrivalTimesArr[1])
        );

        return resultsArr;
    }

    private static Double calculateAveragePrice(int[] ticketsPrices) {
        return Arrays.stream(ticketsPrices)
                .mapToDouble(Double::valueOf)
                .average()
                .orElseThrow(IllegalArgumentException::new);
    }

    private static Double calculateMedianPrice(int[] ticketsPrices) {
        Arrays.sort(ticketsPrices);

        if (ticketsPrices.length % 2 != 0) {
            return (double) ticketsPrices[ticketsPrices.length / 2];
        }
        return (double) (ticketsPrices[ticketsPrices.length / 2] + ticketsPrices[ticketsPrices.length / 2 + 1]) / 2;
    }
}
