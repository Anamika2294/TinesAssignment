import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TinesMainClass {
    private final OkHttpClient httpClient = new OkHttpClient();
    HashMap<String, JSONObject> jsonObjectToKey= new HashMap<>();

    public static void main(String[] args) throws Exception {


        if(args[0].length() !=0){
            String jsonData = readFile(args[0]);
            JSONObject jobj = new JSONObject(jsonData);
        //    System.out.println(jsonData);
            JSONArray jsonArrayAgents=jobj.getJSONArray("agents");
            TinesMainClass obj = new TinesMainClass();

            for(int i=0;i< jsonArrayAgents.length();i++){
                String type=jsonArrayAgents.getJSONObject(i).getString("type");
                if(type.equals("HTTPRequestAgent")){
                    String url=jsonArrayAgents.getJSONObject(i).getJSONObject("options").getString("url");
                    String name=jsonArrayAgents.getJSONObject(i).getString("name");

                    if(i==0){
                        obj.sendGet(url,name);
                    }
                    else {
                        obj.splitandSendData(url,name);
                    }
                }
                else{
                    String message=jsonArrayAgents.getJSONObject(i).getJSONObject("options").getString("message");
                    obj.splitandPrintData(message);

                }

            }

        }


    }

    public void splitandSendData(String url,String name) throws Exception {
        ArrayList<String> arrayList=SplitDataFromUrl(url);
      //  System.out.println("Keys"+arrayList);
        ArrayList<String> values= splitDataAndGetObject(arrayList);
        //System.out.println("Values"+values);

        String updatedUrl=UpdatedUrl(arrayList,values,url);
        sendGet(updatedUrl,name);

    }

    public void splitandPrintData(String message){
        //String message=jsonArrayAgents.getJSONObject(i).getJSONObject("options").getString("message");
        ArrayList<String> arrayList=SplitDataFromUrl(message);
     //   System.out.println("MessageKeys"+arrayList);

        ArrayList<String> values= splitDataAndGetObject(arrayList);
       // System.out.println("MessageValues"+values);

        String updatedMessage=UpdatedUrl(arrayList,values,message);
        System.out.println(updatedMessage);
    }



    public String sendGet(String url, String name) throws Exception {
        String res="";
        Request request = new Request.Builder()
                .url(url)

                   .addHeader("Content-Type", "application/json")
                   .addHeader("Accept", "application/json")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Get response body
            res = response.body().string();

           // System.out.println(res);
            JSONObject jobj = new JSONObject(res);

            jsonObjectToKey.put(name,jobj);

            //System.out.println("JsonObjectToKey"+jsonObjectToKey);



        }
      return  res;
    }



    public static String readFile(String filename) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String UpdatedUrl(ArrayList<String> keysToChanged, ArrayList<String> changedValues,String url){
        HashMap<String, String> hm = new HashMap();
        for(int i=0;i< keysToChanged.size();i++){
            hm.put(keysToChanged.get(i), changedValues.get(i));
        }
        //System.out.println("HashmapList"+hm);


        Pattern p = Pattern.compile("\\{\\{(.*?)\\}\\}");
        Matcher m = p.matcher(url);
        while (m.find()) {
           // System.out.println(m.group());
            String val1 = m.group().replace("{{", "").replace("}}", "");
            //System.out.println(val1);
            url = (url.replace(m.group(), hm.get(val1)));
            //System.out.println(url);

        }
        return url;
    }



    public ArrayList<String> SplitDataFromUrl(String s)
    {

      //  String x= "Hello (Java)";
        Pattern pattern = Pattern.compile("\\{\\{(.*?)\\}\\}");
        ArrayList<String> dataInBrackets = new ArrayList<String>();
        Matcher matchPattern = pattern.matcher(s);

        while(matchPattern.find()) {
          //
            //  System.out.println(matchPattern.group(1));
            dataInBrackets.add(matchPattern.group(1));
        }

      return dataInBrackets;
    }



    public ArrayList<String> splitDataAndGetObject(ArrayList<String> dataInBrackets){
        ArrayList<String> valuesToKeys= new ArrayList<>();
                for(int i=0;i< dataInBrackets.size();i++) {

                    String[] data = dataInBrackets.get(i).split("\\.");
                    //System.out.println("hashmapdata" + data[0]+""+data[1]);

                    JSONObject jsonObject=jsonObjectToKey.get(data[0].trim());
                    //System.out.println("jsonObject"+jsonObject);

                    if(data.length<3){
                        if(jsonObject.get(data[1].trim()) instanceof String){
                            String values=jsonObject.getString(data[1].trim());
                            valuesToKeys.add(values);
                        }
                        else{
                            int values=jsonObject.getInt(data[1].trim());
                            valuesToKeys.add(""+values);

                        }
                    }
                    else{

                        JSONObject obj = jsonObject.getJSONObject(data[1].trim());
                        if(jsonObject.get(data[1].trim()) instanceof String) {
                            String values = obj.getString(data[2].trim());
                            valuesToKeys.add(values);
                        }
                        else{
                            int values=jsonObject.getInt(data[2].trim());
                            valuesToKeys.add(""+values);

                        }
                    }
                }
        return valuesToKeys;
    }
}
