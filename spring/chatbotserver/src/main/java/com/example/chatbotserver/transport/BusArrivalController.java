package com.example.chatbotserver.transport;

import com.example.chatbotserver.service.BusApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

/**
 * Flutter(또는 TransportChatService)에서 POST 요청을 보낼 때 사용하는 엔드포인트입니다.
 * URL: POST http://localhost:8080/api/bus/arrival
 * Body(JSON 예시):
 * {
 *   "stationName": "강북소방서앞 정류장",
 *   "busNumber": "급행2"
 * }
 *
 * 이 컨트롤러는 BusApiClient.getArrivalTime(...)을 호출하여
 * 공공버스 OpenAPI에서 가져온 “원본 JSON”을 파싱하고,
 * 최종적으로 { "data": "OO번 버스는 △△정류장에 ○분 후 도착합니다." } 형식으로 리턴합니다.
 */
@RestController
@AllArgsConstructor
public class BusArrivalController {

    private final BusApiClient busApiClient;

    /**
     * 요청 바디 예시:
     * {
     *   "stationName": "강북소방서앞 정류장",
     *   "busNumber": "급행2"
     * }
     */
    @PostMapping(path = "/api/bus/arrival", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getBusArrival(@RequestBody ArrivalRequest req) {
        String stationName = req.getStationName().trim();
        String busNumber   = req.getBusNumber().trim();

        if (stationName.isEmpty() || busNumber.isEmpty()) {
            return Collections.singletonMap("data", "정류장 이름과 버스 번호를 정확히 입력해주세요.");
        }

        // 1) 공공버스 OpenAPI를 호출하여 “raw” JSON 문자열을 받아온다.
        String rawJsonResponse;
        try {
            rawJsonResponse = busApiClient.getArrivalTime(stationName, busNumber);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.singletonMap("data", "버스 도착 정보를 가져오는 중 오류가 발생했습니다: " + ex.getMessage());
        }

        // 2) rawJsonResponse에는 공공버스 API가 돌려주는 “원본 JSON”이 들어 있다.
        //    (예시: { "list":[ {"rtNm":"650","arrmsg1":"3분 후 도착", …}, … ], … } 등)
        //    실제 필드명은 대구시 버스정보시스템 OpenAPI 문서를 참고하여 수정하세요.
        //
        //    아래 코드는 Jackson ObjectMapper를 이용해 간단히 parsing한 뒤,
        //    첫 번째 list 객체의 arrmsg1(도착예정문구)만 꺼내서 최종 문자열로 만든 예시입니다.
        //
        String formattedText;
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(rawJsonResponse);
            JsonNode listNode = root.get("list");
            if (listNode != null && listNode.isArray() && listNode.size() > 0) {
                JsonNode first = listNode.get(0);
                String rtNm    = first.path("rtNm").asText();      // 버스번호
                String arrMsg1 = first.path("arrmsg1").asText();  // “○분 후 도착” 형태
                formattedText = rtNm + "번 버스는 " + arrMsg1 + "입니다.";
            } else {
                formattedText = busNumber + "번 버스 도착 정보를 찾을 수 없습니다.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            formattedText = "버스 도착 정보를 파싱하는 중 오류가 발생했습니다.";
        }

        // 3) 최종 JSON으로 돌려준다.
        return Collections.singletonMap("data", formattedText);
    }
}

/**
 * POST /api/bus/arrival 요청이 JSON 바디 형태로 들어올 때 파싱하기 위한 DTO 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class ArrivalRequest {
    private String stationName;
    private String busNumber;
}
