package top.giftedcc.features;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chang on 2018/11/24.
 * Class:  数据上传类
 */

public class HTTP_Post {

    public List<String> uploadDataByHttp_Post(String url, ArrayList<NameValuePair> pairs){
        //int result = 0;
        //1.生成HttpClient对象
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);//请求超时时间3秒
        //2.生成HttpPost对象
        HttpPost httppost = new HttpPost(url);
        List<String> resList = new ArrayList<>(2);
        try {
            //System.out.println("HTTP_Post-->url = " + url);

            //3.创建一个请求体，把ArrayList放入当做请求体的参数
            HttpEntity reqentity = new UrlEncodedFormEntity(pairs, HTTP.UTF_8);
            //4.把请求体放入HttpPost当中
            httppost.setEntity(reqentity);
            //5.发送HttpPost请求
            HttpResponse resp = httpclient.execute(httppost);
            //System.out.println("getStatueCode--->" + resp.getStatusLine().getStatusCode());
            //String res_01 = "";
            JSONObject jsonObject = null;
            //StringBuilder entityStringBuilder = new StringBuilder();
            String strResult = "";
            if(resp.getStatusLine().getStatusCode() == 200){
                //System.out.println("response-----> before");
                //result = 1;
                // 得到HttpResponse的实体数据
                strResult = EntityUtils.toString(resp.getEntity());
                //Log.d("http","数据是"+result+"服务器返回: "+res_01);
            }
            //System.out.println("response--> insert count=" + res_01 + "  result=" + result);
            // 解析json数据
            jsonObject = new JSONObject(strResult);

            String success = jsonObject.getString("success");
            String failId = jsonObject.getString("failId");
            resList.add(success);
            resList.add(failId);
        } catch (Exception e) {
            //System.out.println("HTTP_Post--->error.");
            e.printStackTrace();
            return null;
        }
        return resList;
    }
}
