package org.nycu.mc.iwf.radio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.sip.InvalidArgumentException;
import javax.sip.SipException;

import org.json.JSONObject;
import org.nycu.mc.iwf.main.UserSession;

import java.util.HashMap;
import java.util.Map;

import java.util.HashMap;
import java.util.Map;

public class WebSocketRouter {
    private WebSocketHandler handler;
    private Map<String, UserSession> sessionMap = new HashMap<>();
    private String Radioserverip;

    public WebSocketRouter(String radioserverip) {
        handler = new WebSocketHandler(this);
        Radioserverip = radioserverip;
    }

    public void connect(String url) {
        handler.connect(url);
    }

    public void registerSession(String userId, UserSession session) {
        sessionMap.put(userId, session);
    }

    public void unregisterSession(String userId) {
        sessionMap.remove(userId);
    }

    public void sendMessage(String userId, String type, String message, boolean test) {
        // 你可以選擇在 message 中封裝 userId，例如 JSON 格式
    	 System.out.println("userId: " + userId);
    	 System.out.println("type: " + type);
    	 System.out.println("message: " + message);
    	 System.out.println("group?: " + test);
    	 switch (type) {
	         case "sds":
	        	 sendHttpsPostRequest(mapUserId(userId, test), "api/radp/sendtext", message, test);
	             break;
	             
	         default:
	             System.err.println("未知的 opcode: " );
	             break;
    	 }
    }

    
    public void routeMessage(String rawMessage) {
        System.out.println("rawMessage: " + rawMessage);

        try {
            JSONObject json = new JSONObject(rawMessage);
            String opcode = json.getString("opcode");

            switch (opcode) {
                case "OPC_DevStatus":
                    handleDevStatus(json.getJSONObject("message"));
                    break;

                case "OPC_RequestResp":
                    handleRequestResp(json.getJSONObject("message"));
                    break;

                case "OPC_SOV":
                    handleSOV(json.getJSONObject("message"));
                    break;

                case "OPC_Text":
                    handleText(json.getJSONObject("message"));
                    break;

                case "OPC_VOICE":
                    handleVOICE(json.getJSONObject("message"));
                    break;

                case "OPC_EOV":
                    handleEOV(json.getJSONObject("message"));
                    break;
                    
                default:
                    System.err.println("未知的 opcode: " + opcode);
                    break;
            }

        } catch (Exception e) {
            System.err.println("無法解析訊息: " + rawMessage);
            e.printStackTrace();
        }
    }

	private void handleEOV(JSONObject jsonObject) {
		JSONObject header = jsonObject.getJSONObject("Header");
	    String from = header.getString("From");
	    String to = header.getString("To");
	    String ConName = jsonObject.getString("ConName");
	    int DeviceId = header.getInt("DeviceId");
	    int DialType = header.getInt("DialType");
	    int ConReqId = header.getInt("ConReqId");
	    int status = jsonObject.getInt("Status");
	
	    
	    System.out.println("From: " + from);
	    System.out.println("To: " + to);
	    System.out.println("ConName: " + ConName);
	    System.out.println("DeviceId: " + DeviceId);
	    System.out.println("DialType: " + DialType);
	    System.out.println("ConReqId: " + ConReqId);
//	
//	      // 可根據 From 當作 userId
//	      UserSession session = sessionMap.get(from);
//	      if (session != null) {
//	          session.receiveMessage(rawMessage); // 或其他你想傳送的部分
//	      } else {
//	          System.out.println("找不到對應的 UserSession: " + from);
//	      }
//			
	}

	private void handleVOICE(JSONObject jsonObject) {
		JSONObject header = jsonObject.getJSONObject("Header");
	    String from = header.getString("From");
	    String to = header.getString("To");
	    String ConName = jsonObject.getString("ConName");
	    int DeviceId = header.getInt("DeviceId");
	    int DialType = header.getInt("DialType");
	    int ConReqId = header.getInt("ConReqId");
	    String VoiceData = jsonObject.getString("Status");
	
	    
	    System.out.println("From: " + from);
	    System.out.println("To: " + to);
	    System.out.println("ConName: " + ConName);
	    System.out.println("DeviceId: " + DeviceId);
	    System.out.println("DialType: " + DialType);
	    System.out.println("ConReqId: " + ConReqId);
	    System.out.println("VoiceData: " + VoiceData);
//      // 可根據 From 當作 userId
//      UserSession session = sessionMap.get(from);
//      if (session != null) {
//          session.receiveMessage(rawMessage); // 或其他你想傳送的部分
//      } else {
//          System.out.println("找不到對應的 UserSession: " + from);
//      }
		
	}

	private void handleText(JSONObject jsonObject) throws ParseException, InvalidArgumentException, SipException {
		JSONObject header = jsonObject.getJSONObject("Header");
	    String from = header.getString("From");
	    String to = header.getString("To");
	    String ConName = jsonObject.getString("ConName");
	    int DeviceId = header.getInt("DeviceId");
	    int DialType = header.getInt("DialType");
	    int ConReqId = header.getInt("ConReqId");
	    JSONObject msgObject = jsonObject.getJSONObject("Msg");
	    String msgContent = msgObject.getString("Contect");
	
	    
	    System.out.println("From: " + from);
	    System.out.println("To: " + to);
	    System.out.println("ConName: " + ConName);
	    System.out.println("DeviceId: " + DeviceId);
	    System.out.println("DialType: " + DialType);
	    System.out.println("ConReqId: " + ConReqId);
	    System.out.println("Msg: " + msgContent);
//
//      // 可根據 From 當作 userId
      UserSession session = sessionMap.get(remapUserId(from));
      if (session != null) {
          session.receiveMessage(msgContent, DialType, remapUserId(to)); // 或其他你想傳送的部分


      } else {
          System.out.println("找不到對應的 UserSession: " + from);
      }
		
	}

	private void handleSOV(JSONObject jsonObject) {
		JSONObject header = jsonObject.getJSONObject("Header");
	    String from = header.getString("From");
	    String to = header.getString("To");
	    String ConName = jsonObject.getString("ConName");
	    int DeviceId = header.getInt("DeviceId");
	    int DialType = header.getInt("DialType");
	    int ConReqId = header.getInt("ConReqId");
		    
	    System.out.println("From: " + from);
	    System.out.println("To: " + to);
	    System.out.println("ConName: " + ConName);
	    System.out.println("DeviceId: " + DeviceId);
	    System.out.println("DialType: " + DialType);
	    System.out.println("ConReqId: " + ConReqId);
//
//      // 可根據 From 當作 userId
//      UserSession session = sessionMap.get(from);
//      if (session != null) {
//          session.receiveMessage(rawMessage); // 或其他你想傳送的部分
//      } else {
//          System.out.println("找不到對應的 UserSession: " + from);
//      }
		
	}

	private void handleRequestResp(JSONObject jsonObject) {
		JSONObject header = jsonObject.getJSONObject("Header");
	    String from = header.getString("From");
	    String to = header.getString("To");
	    String ConName = jsonObject.getString("ConName");
	    int DeviceId = header.getInt("DeviceId");
	    int DialType = header.getInt("DialType");
	    int ConReqId = header.getInt("ConReqId");
	    int RequestID = jsonObject.getInt("RequestID");
	    int ErrorCode = jsonObject.getInt("ErrorCode");
	
	    
	    System.out.println("From: " + from);
	    System.out.println("To: " + to);
	    System.out.println("ConName: " + ConName);
	    System.out.println("DeviceId: " + DeviceId);
	    System.out.println("DialType: " + DialType);
	    System.out.println("ConReqId: " + ConReqId);
	    System.out.println("RequestID: " + RequestID);
	    System.out.println("ErrorCode: " + ErrorCode);
//
//      // 可根據 From 當作 userId
//      UserSession session = sessionMap.get(from);
//      if (session != null) {
//          session.receiveMessage(rawMessage); // 或其他你想傳送的部分
//      } else {
//          System.out.println("找不到對應的 UserSession: " + from);
//      }
		
	}

	private void handleDevStatus(JSONObject jsonObject) {
		JSONObject header = jsonObject.getJSONObject("Header");
	    String from = header.getString("From");
	    String to = header.getString("To");
	    String ConName = jsonObject.getString("ConName");
	    int DeviceId = header.getInt("DeviceId");
	    int DialType = header.getInt("DialType");
	    int ConReqId = header.getInt("ConReqId");
	    int status = jsonObject.getInt("Status");
	    
	    System.out.println("From: " + from);
	    System.out.println("To: " + to);
	    System.out.println("ConName: " + ConName);
	    System.out.println("DeviceId: " + DeviceId);
	    System.out.println("DialType: " + DialType);
	    System.out.println("ConReqId: " + ConReqId);
	    System.out.println("status: " + status);
//
//      // 可根據 From 當作 userId
//      UserSession session = sessionMap.get(from);
//      if (session != null) {
//          session.receiveMessage(rawMessage); // 或其他你想傳送的部分
//      } else {
//          System.out.println("找不到對應的 UserSession: " + from);
//      }
		
	}
	
	public String sendHttpsPostRequest(String uid, String type, String message, boolean grouporone) {
	    try {
	    	System.out.println("uid"+uid);
	        // 👉 關閉 SSL 驗證（只用於測試環境！）
	        TrustManager[] trustAllCerts = new TrustManager[]{
	            new X509TrustManager() {
	                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
	                public void checkClientTrusted(X509Certificate[] certs, String authType) { }
	                public void checkServerTrusted(X509Certificate[] certs, String authType) { }
	            }
	        };
	        SSLContext sc = SSLContext.getInstance("TLS");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

	        Map<String, String> paramsWithMsg = new HashMap<>();
	        paramsWithMsg.put("ConName", "");
	        paramsWithMsg.put("From", "0");
	        paramsWithMsg.put("To", uid);
	        paramsWithMsg.put("DeviceId", "0");
	        if(grouporone) {
	        	paramsWithMsg.put("DialType", "2");
	        }else {
	        	paramsWithMsg.put("DialType", "1");
	        }
	        paramsWithMsg.put("ConReqId", "1");
	        if(type.equals("api/radp/sendtext")) {
	        	paramsWithMsg.put("Msg", message);
	        }
	        String queryParams = buildQueryParams(paramsWithMsg);
	        
	        String postUrl = "https://"+Radioserverip+"/" + type + "?" + queryParams;
	        System.out.println("[post]"+postUrl);
	        URL url = new URL(postUrl);
	        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setDoOutput(true);
	        connection.setDoInput(true);
	        connection.setRequestProperty("Accept", "application/json");

	        int responseCode = connection.getResponseCode();

	        if (responseCode == HttpsURLConnection.HTTP_OK) {
	            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
	                StringBuilder response = new StringBuilder();
	                String inputLine;
	                while ((inputLine = in.readLine()) != null) {
	                    response.append(inputLine);
	                }
	                System.out.println("[response] " + response.toString());
	                return response.toString();
	            }
	        } else {
	            return "Request failed with code: " + responseCode;
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        return "Exception occurred: " + e.getMessage();
	    }
	}


	
	public static String buildQueryParams(Map<String, String> params) throws UnsupportedEncodingException {
	    StringBuilder query = new StringBuilder();

	    for (Map.Entry<String, String> entry : params.entrySet()) {
	        if (entry.getValue() != null && !entry.getValue().isEmpty()) {
	            if (query.length() > 0) {
	                query.append("&");
	            }
	            query.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
	                 .append("=")
	                 .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
	        }
	    }

	    return query.toString();
	}

	private String mapUserId(String userId, boolean grouporone) {
	    Map<String, String> userIdMap = new HashMap<>();
	    if(grouporone) {
		    userIdMap.put("2024_3_21_341949@140.113.110.221", "11");
		    userIdMap.put("2024_3_21_435882@140.113.110.221", "22");
		    userIdMap.put("test3", "33"); // ➕ 你可以擴充更多對應
	    }else {
	    	userIdMap.put("test1", "2");
		    userIdMap.put("test2", "3");
		    userIdMap.put("test3", "33"); 
	    }
	    return userIdMap.getOrDefault(userId, userId); // 如果沒對應就回傳原本 userId
	}
	
	private String remapUserId(String userId) {
	    Map<String, String> userIdMap = new HashMap<>();
	    
		    userIdMap.put("11", "2024_3_21_341949@140.113.110.221");
		    userIdMap.put("22", "2024_3_21_435882@140.113.110.221");
		    userIdMap.put("33", "2024_3_21_435882@140.113.110.221");
		    userIdMap.put("55", "2024_3_21_341949@140.113.110.221");
		    userIdMap.put("2", "test1");
		    userIdMap.put("3", "test2");// ➕ 你可以擴充更多對應
	    
	    return userIdMap.getOrDefault(userId, userId); // 如果沒對應就回傳原本 userId
	}

}
