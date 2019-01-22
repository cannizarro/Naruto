package com.example.asrarulhaque.guessthecharachter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView profilePic;
    Button b0,b1,b2,b3;
    int ansOpIndex,ansIndex,streak=0;
    TextView streakText;

    ArrayList<String> characterUrls=new ArrayList<String>();
    ArrayList<String> characterNames=new ArrayList<String>();

    public class GetHttp extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... urls) {

            String result="";
            URL url;
            HttpURLConnection connection=null;

            try {
                url=new URL(urls[0]);
                connection=(HttpURLConnection)url.openConnection();
                InputStream in=connection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);

                int data=reader.read();

                while(data != -1)
                {
                    char current=(char)data;
                    result += current;
                    data=reader.read();
                }

                Pattern p=Pattern.compile("<P><A href=\"allchars.php?id=100053&amp;tile&amp;alpha\">Order by name?</A></P>(.*?)googletag.defineSlot('/53015287/animecharactersdatabase.com_d_300x250_1', [300, 250], 'div-gpt-ad-1407836219176-0').addService(googletag.pubads());");
                Matcher m=p.matcher(result);
                while(m.find())
                {
                    result=m.group(1);
                }

                p=Pattern.compile("<IMG SRC=\"(.*?)\" ");
                m=p.matcher(result);
                while(m.find())
                {

                    characterUrls.add(m.group(1));
                }

                p=Pattern.compile("ALT=\"(.*?)\"></A>");
                m=p.matcher(result);
                m.find();
                while(m.find())
                {
                    characterNames.add(m.group(1));
                }
                return "Success";

            }
            catch(Exception e)
            {
                e.printStackTrace();
                return "Failed (Check your Internet Connection)";
            }

        }
    }

    public class SetImage extends AsyncTask<String,Void,Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... urls) {

            URL url;
            Bitmap profile;
            HttpURLConnection connection;
            try
            {
                url=new URL(urls[0]);
                connection=(HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream in=connection.getInputStream();
                profile=BitmapFactory.decodeStream(in);
                return profile;
            }
            catch(Exception e)
            {
                e.printStackTrace();
                return null;
            }

        }
    }

    public void random()
    {
        Random rand=new Random();
        SetImage image=new SetImage();
        Bitmap profile;
        int nameOpIndex[]=new int[4];       //Button Index in terms of name shown
        for(int i=0;i<4;i++)
        {
            nameOpIndex[i]=rand.nextInt(403);
        }
        ansOpIndex=rand.nextInt(3);     //Index of the button in which the answer is stored
        ansIndex=nameOpIndex[ansOpIndex];       //Index of right answer in the characterNames arrayList
        b0.setText(characterNames.get(nameOpIndex[0]));
        b1.setText(characterNames.get(nameOpIndex[1]));
        b2.setText(characterNames.get(nameOpIndex[2]));
        b3.setText(characterNames.get(nameOpIndex[3]));
        //String characterName=characterNames.get(ansIndex);
        //String characterUrl=characterUrls.get(ansIndex);
        try
        {
            profile=image.execute(characterUrls.get(ansIndex)).get();
            profilePic.setImageBitmap(profile);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public void guess(View v)
    {
        int tag=Integer.parseInt((String) v.getTag());
        if(tag == ansOpIndex)
        {
            streak++;
            streakText.setText("Streak = "+String.valueOf(streak));
            Toast.makeText(this, "Correct :)", Toast.LENGTH_LONG).show();
        }
        else
        {
            streak=0;
            streakText.setText("Streak = 0");
            Toast.makeText(this, "Wrong!!", Toast.LENGTH_LONG).show();
        }
        random();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b0=(Button)findViewById(R.id.b0);
        b1=(Button)findViewById(R.id.b1);
        b2=(Button)findViewById(R.id.b2);
        b3=(Button)findViewById(R.id.b3);
        streakText=(TextView)findViewById(R.id.streak);

        profilePic=(ImageView)findViewById(R.id.profile);



        GetHttp task=new GetHttp();
        String result;

        try
        {
            result=task.execute("https://www.animecharactersdatabase.com/allchars.php?id=100053").get();
            //Toast.makeText(this, result, Toast.LENGTH_SHORT).show();

        }
        catch (Exception e)

        {
            e.printStackTrace();
        }
        random();


    }
}
