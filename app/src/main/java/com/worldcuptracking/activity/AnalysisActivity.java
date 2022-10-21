package com.worldcuptracking.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.worldcuptracking.R;
import com.worldcuptracking.utils.Tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Locale;

public class AnalysisActivity extends AppCompatActivity {

    String link;
    String content;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initToolbar();
        TextView channel = findViewById(R.id.channel);
        channel.setText("FIFA.com Analysis");
        Intent intent = getIntent();
        link = intent.getStringExtra("link");

        HtmlParser htmlThread = new HtmlParser();
        htmlThread.execute();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    // load links in WebView instead of default browser
    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //view.loadUrl(url);
            return true;
        }

    }

    ProgressDialog dialog;

    public class HtmlParser extends AsyncTask<Void, Integer, String> {

        private static final int NETWORK_NO_ERROR = -1;
        private static final int NETWORK_HOST_UNREACHABLE = 1;
        private static final int NETWORK_NO_ACCESS_TO_INTERNET = 2;
        private static final int NETWORK_TIME_OUT = 3;

        Integer serverError = NETWORK_NO_ERROR;
        private String imgUrl;

        protected void onPreExecute() {
            dialog = new ProgressDialog(AnalysisActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading...");
            dialog.show();
        } // end onPreExecute

        @Override
        protected String doInBackground(Void... params) {
            try {
                // need http protocol
                Document doc = Jsoup.connect(link).get();


                if (Locale.getDefault().getLanguage().equals("ar")) {

                    if (doc != null) {
                    imgUrl = doc.getElementsByTag("picture").select("img").attr("src");
                    Element element = doc.getElementsByClass("clearfix text-formatted field field--name-body field--type-text-with-summary field--label-hidden field__item").first();
                    //element.getElementsByTag("a").remove();
                    if (element != null) {
                        element.getElementsByClass("align-center.embedded-entity").remove();
                        element.getElementsByClass("a2a_kit a2a_kit_size_32 addtoany_list").remove();
                        element.getElementsByClass("embedded-entity").remove();

                        //datetime = doc.getElementsByClass("article-credits").get(0).text();
                        // replace body with selected element
                        doc.body().empty().append(element.toString());
                        return element.html();
                    }
                }
                }else{
                    if (doc != null) {
                        imgUrl = doc.getElementsByTag("picture").select("img").attr("data-src");
                        Element element = doc.getElementsByClass("d3-o-article__body fi-article__body").first();
                        //element.getElementsByTag("a").remove();
                        if (element != null) {
                            element.getElementsByClass("fi-video-container").remove();
                            //datetime = doc.getElementsByClass("article-credits").get(0).text();
                            // replace body with selected element
                            doc.body().empty().append(element.toString());
                            return element.html();
                        }
                    }
            }
            } catch (ConnectException e) {
                serverError = NETWORK_NO_ACCESS_TO_INTERNET;
                return null;
            } catch (UnknownHostException e) {
                serverError = NETWORK_HOST_UNREACHABLE;
                return null;
            } catch (SocketTimeoutException e) {
                serverError = NETWORK_TIME_OUT;
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();

            } // end try catch
            return null;

        } // end doInBackground

        protected void onProgressUpdate(Integer... progress) {

        } // end onProgressUpdate

        protected void onPostExecute(String result) {
            if (result != null) {

                ImageView image = findViewById(R.id.image);
                image.setScaleType(ImageView.ScaleType.FIT_XY);
                Tools.displayImageOriginal(AnalysisActivity.this, image, imgUrl);
                TextView titleTxt = findViewById(R.id.title);
                titleTxt.setVisibility(View.GONE);
                TextView dateTxt = findViewById(R.id.date);
                dateTxt.setVisibility(View.GONE);
                WebView contentTxt = findViewById(R.id.content);
                contentTxt.getSettings().setJavaScriptEnabled(true);
                contentTxt.setWebViewClient(new AnalysisActivity.MyWebViewClient());


                if (Locale.getDefault().getLanguage().equals("ar")) {
                    content = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "<html><head>" +
                            "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />" +
                            "</head><body style='text-align: justify;direction: rtl;'>";
                }else {
                    content =
                            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "<html><head>" +
                                    "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />" +
                                    "</head><body style='text-align: justify;'>";
                }

                content += result + "</body></html>";
                contentTxt.loadData(content, "text/html; charset=utf-8", "UTF-8");
                menu.findItem(R.id.action_refresh).setVisible(false);
            } else {
                switch (serverError) {
                    case NETWORK_NO_ERROR:
                        Toast.makeText(AnalysisActivity.this, "Probably, invalid response from server", Toast.LENGTH_LONG).show();
                        break;
                    case NETWORK_NO_ACCESS_TO_INTERNET:
                        // You can customize error message (or behavior) for
                        // different type of error
                    case NETWORK_TIME_OUT:
                    case NETWORK_HOST_UNREACHABLE:
                        Toast.makeText(AnalysisActivity.this, "Error in Connection", Toast.LENGTH_LONG).show();
                        break;
                }
            } // end if else

            if (dialog.isShowing()) {
                dialog.dismiss();
            } // end if
        } // end onPostExecute
    } // end HtmlParser class
} // end NewsFeeds


