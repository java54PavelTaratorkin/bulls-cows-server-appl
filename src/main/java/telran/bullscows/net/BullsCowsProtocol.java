package telran.bullscows.net;

import org.json.*;

import telran.bullscows.*;
import telran.net.*;

import java.util.List;
import java.util.stream.Collectors;

public class BullsCowsProtocol implements Protocol {
    private final BullsCowsService service;

    public BullsCowsProtocol(BullsCowsService service) {
        this.service = service;
    }

    @Override
    public Response getResponse(Request request) {
        try {
            switch (request.requestType()) {
                case "createNewGame":
                    return handleCreateNewGame();
                case "getResults":
                    return handleGetResults(request.requestData());
                case "isGameOver":
                    return handleIsGameOver(request.requestData());
                default:
                    return new Response(ResponseCode.WRONG_REQUEST_TYPE, "Unknown request type");
            }
        } catch (Exception e) {
            return new Response(ResponseCode.WRONG_REQUEST_DATA, e.getMessage());
        }
    }

    private Response handleCreateNewGame() {
        long gameId = service.createNewGame();
        return new Response(ResponseCode.OK, String.valueOf(gameId));
    }

    private Response handleGetResults(String requestData) {
        JSONObject jsonObj = new JSONObject(requestData);
        long gameId = jsonObj.getLong("gameId");
        String clientSequence = jsonObj.getString("clientSequence");
        Move move = new Move(gameId, clientSequence);
        List<MoveResult> results = service.getResults(gameId, move);
        String resultsString = moveResultsToString(results);
        return new Response(ResponseCode.OK, resultsString);
    }

    private Response handleIsGameOver(String requestData) {
        long gameId = Long.parseLong(requestData);
        boolean isGameOver = service.isGameOver(gameId);
        return new Response(ResponseCode.OK, String.valueOf(isGameOver));
    }

    private String moveResultsToString(List<MoveResult> results) {
        return results.stream()
                .map(result -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("clientSequence", result.clientSequence());
                    jsonObject.put("bulls", result.bulls());
                    jsonObject.put("cows", result.cows());
                    return jsonObject.toString();
                })
                .collect(Collectors.joining(";"));
    }
}
