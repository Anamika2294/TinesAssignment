
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;

public class TinesTest {

//    ClassLoader classLoader = getClass().getClassLoader();
//    File file = new File(classLoader.getResource("tiny-tines-sunset.json").getFile());
//    InputStream is = new FileInputStream(file);
//    String jsonTxt = IOUtils.toString(is);
//    int i = jsonTxt.indexOf("{");
//    jsonTxt = jsonTxt.substring(i);
//    JSONObject jsonObject = new JSONObject(jsonTxt);
//
//    String url=jsonObject.getJSONArray("agents").getJSONObject(0).
//            getJSONObject("options").getString("url");
//    TinesMainClass obj= new TinesMainClass();

//check api response
TinesMainClass obj= new TinesMainClass();
    ArrayList<String> expectedArraylist= new ArrayList<>();
    ArrayList<String> keysToChange= new ArrayList<>();
    ArrayList<String> changedValues= new ArrayList<>();
    String url;

    HashMap<String, JSONObject> jsonObjectToKey= new HashMap<>();



    @Test
    public  void checkAPICall() throws Exception {
        String response=obj.sendGet("http://free.ipwhois.io/json/","location");
        JSONObject jobject= new JSONObject(response);
       // System.out.println(jobject.getBoolean("success"));
        jsonObjectToKey.put("location",jobject);
        Assert.assertEquals(true,jobject.getBoolean("success"));
    }

    //data Split at double curly brackets
    @Test
    public void checkDataSplit() {
        url="https://api.sunrise-sunset.org/json?lat={{ location.latitude }}&lng={{ location.longitude }}";
        ArrayList<String> observedArraylist=obj.SplitDataFromUrl(url);
        expectedArraylist.add(" location.latitude ");
        expectedArraylist.add(" location.longitude ");
        Assert.assertEquals(expectedArraylist,observedArraylist);
    }

    // spliting data to find out in jsonObject
    @Test
    public void checkUpdatedUrl() {
        url="https://api.sunrise-sunset.org/json?lat={{ location.latitude }}&lng={{ location.longitude }}";

        keysToChange.add(" location.latitude ");
        keysToChange.add(" location.longitude ");

        changedValues.add("53.3165322");
        changedValues.add("-6.3425318");

        String observedUrl=obj.UpdatedUrl(keysToChange,changedValues,url);
        String expectedUrl="https://api.sunrise-sunset.org/json?lat=53.3165322&lng=-6.3425318";
       // System.out.println("splittedData"+observedUrl);

        Assert.assertEquals(expectedUrl,observedUrl);


    }







}
