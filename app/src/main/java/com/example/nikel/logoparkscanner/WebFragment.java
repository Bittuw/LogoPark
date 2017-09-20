package com.example.nikel.logoparkscanner;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by nikel on 09.09.2017.
 */

public class WebFragment extends Fragment{
    TextView contentView;
    String contentText = null;
    WebView webView;
    private String username;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web, container, false);
        contentView = view.findViewById(R.id.content);
        webView = view.findViewById(R.id.webView);

        return view;
    }

    //Сохранение
    @Override
    public void onPause(){
        super.onPause();
    }

    public void DownloadInfo(String url, String code) {

        if(contentText==null){
            contentView.setText("Загрузка...");
            url = code + "?key=" + "123456789";
            new ProgressTask().execute(url);
        }
        else {
            DoEvent();
            /*Toast.makeText(getActivity(), "Данные уже загружены загружены", Toast.LENGTH_SHORT)
                    .show();*/
        }
    }

    private class ProgressTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... path) {

            String content;
            try{
                content = getContent(path[0]);
            }
            catch (IOException ex){
                content = ex.getMessage();
            }

            return content;
        }
        @Override
        protected void onPostExecute(String content) {
            DoEvent();
            contentText=content;
            contentView.setText(content);
            webView.loadData(content, "text/html; charset=utf-8", "utf-8");
            Toast.makeText(getActivity(), "Данные загружены", Toast.LENGTH_SHORT)
                    .show();
        }

        private String getContent(String path) throws IOException {
            BufferedReader reader=null;
            try {
                URL url=new URL(path);
                HttpsURLConnection c=(HttpsURLConnection)url.openConnection();
                c.setRequestMethod("GET");
                c.setReadTimeout(10000);
                c.connect();
                reader= new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuilder buf=new StringBuilder();
                String line;
                 while ((line=reader.readLine()) != null) {
                    buf.append(line + "\n");
                }
                return(buf.toString());
            }
            finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
    }

    //События фрагмента
    public interface FragmentEvents {
        public void downloaded();
    }

    //Вызов слушающих методов
    private void DoEvent() {
        FragmentEvents listner = (FragmentEvents) getActivity();
        listner.downloaded();
    }
}
