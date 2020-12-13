package com.example.mp_final_wookie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

        //생성할 SQLite DB를 DBHelper라는 생성된 클래스를 이용하여 선언하여 생성해주기
        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "STUDENTLIST.db", null, 1);

        // 테이블에 있는 모든 데이터 출력
        final TextView result = (TextView) findViewById(R.id.result);


        // DB에 데이터 추가
        Button insert = (Button) findViewById(R.id.insert);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = "rla";
                double gpa = 3.75;
                int id = 20151458;

                dbHelper.insert(id, name, gpa);
                result.setText(dbHelper.getResult());
            }
        });
    }


    public void btnMethod(View v) {
        NetworkThread thread = new NetworkThread();
        thread.start();
    }

    public class NetworkThread extends Thread {

        @Override
        public void run() {
            try {
                String site = "http://192.168.35.100:8080/BasicServer/xml.jsp";
                URL url = new URL(site);
                URLConnection conn = url.openConnection();

                InputStream is = conn.getInputStream();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(is);

                Element root = doc.getDocumentElement();

                NodeList item_list = root.getElementsByTagName("item");

                for (int i = 0; i < item_list.getLength(); i++) {
                    Element item_tag = (Element) item_list.item(i);
                    NodeList data1_list = item_tag.getElementsByTagName("data1");
                    NodeList data2_list = item_tag.getElementsByTagName("data2");
                    NodeList data3_list = item_tag.getElementsByTagName("data3");

                    Element data1_tag = (Element) data1_list.item(0);
                    Element data2_tag = (Element) data2_list.item(0);
                    Element data3_tag = (Element) data3_list.item(0);

                    String data1 = data1_tag.getTextContent();
                    final String data2 = data2_tag.getTextContent();
                    String data3 = data3_tag.getTextContent();

                    final int a1 = Integer.parseInt(data1);
                    final double a3 = Double.parseDouble(data3);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.append("학번 : " + a1 + "\n");
                            textView.append("이름 : " + data2 + "\n");
                            textView.append("학점 : " + a3 + "\n\n");
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class DBHelper extends SQLiteOpenHelper {

        // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }
        // DB를 새로 생성할 때 호출되는 함수
        @Override
        public void onCreate(SQLiteDatabase db) {
            // 새로운 테이블 생성
            db.execSQL("CREATE TABLE STUDENTLIST (_id INTEGER PRIMARY KEY AUTOINCREMENT, studentID TEXT, name TEXT, gpa REAL);");
        }
        // DB 버전이 변경될 때 호출되는 함수
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        public void insert(int studentID, String name, double gpa) {
            // 읽고 쓰기가 가능하게 DB 열기
            SQLiteDatabase db = getWritableDatabase();
            // DB에 입력한 값으로 행 추가
            db.execSQL("INSERT INTO STUDENTLIST VALUES(null, '" + studentID + "', " + name + ", '" + gpa + "');");
            db.close();
        }
        public String getResult() {
            // 읽기가 가능하게 DB 열기
            SQLiteDatabase db = getReadableDatabase();
            String result = "";

            // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
            Cursor cursor = db.rawQuery("SELECT * FROM STUDENTLIST", null);
            while (cursor.moveToNext()) {
                result += cursor.getString(0)
                        + " : "
                        + cursor.getString(1)
                        + " | "
                        + cursor.getString(2)
                        + " - "
                        + cursor.getDouble(3)
                        + "점\n";
            }

            return result;
        }
    }
}