package com.blueitltd.atoshimessage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    CardView cbgview, ctcview;
    TextView cbg, ctc;

    AlertDialog.Builder chooseprimarybg, choosetextcolor;
    AlertDialog cpbg, ctbg;
    View view1, view2;

    DatabaseHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        cbgview = findViewById(R.id.chooseprivamrybgview);
        ctcview = findViewById(R.id.choosetextcolorview);
        cbg = findViewById(R.id.chooseprivamrybg);
        ctc = findViewById(R.id.choosetextcolor);
        helper = new DatabaseHelper(this);

        chooseprimarybg = new AlertDialog.Builder(this);
        choosetextcolor = new AlertDialog.Builder(this);


        CreateView1();
        CreateView2();


        cbgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cpbg.show();
            }
        });
        cbg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cpbg.show();
            }
        });

        ctcview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ctbg.show();
            }
        });
        ctc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ctbg.show();
            }
        });


    }


    void CreateView1() {
        view1 = getLayoutInflater().inflate(R.layout.choosebackground, null, false);
        chooseprimarybg.setView(view1);
        ImageView bg1, bg2, bg3, bg4, bg5, bg6,bg7,bg8,bg9,bg10;
        bg1 = view1.findViewById(R.id.bg1);
        bg2 = view1.findViewById(R.id.bg2);
        bg3 = view1.findViewById(R.id.bg3);
        bg4 = view1.findViewById(R.id.bg4);
        bg5 = view1.findViewById(R.id.bg5);
        bg6 = view1.findViewById(R.id.bg6);
        bg7 = view1.findViewById(R.id.bg7);
        bg8 = view1.findViewById(R.id.bg8);
        bg9 = view1.findViewById(R.id.bg9);
        bg10 = view1.findViewById(R.id.bg10);

        bg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cpbg.dismiss();
                Variables.backgroundcolor = "bg1";
                MainActivity.changeBg("bg1");
                helper.updatebg("bg1");
            }
        });

        bg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cpbg.dismiss();
                Variables.backgroundcolor = "bg2";
                MainActivity.changeBg("bg2");
                helper.updatebg("bg2");
            }
        });
        bg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cpbg.dismiss();
                Variables.backgroundcolor = "bg3";
                MainActivity.changeBg("bg3");
                helper.updatebg("bg3");
            }
        });
        bg4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cpbg.dismiss();
                Variables.backgroundcolor = "bg4";
                MainActivity.changeBg("bg4");
                helper.updatebg("bg4");
            }
        });
        bg5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cpbg.dismiss();
                Variables.backgroundcolor = "bg5";
                MainActivity.changeBg("bg5");
                helper.updatebg("bg5");
            }
        });
        bg6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cpbg.dismiss();
                Variables.backgroundcolor = "bg6";
                MainActivity.changeBg("bg6");
                helper.updatebg("bg6");
            }
        });
        bg7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cpbg.dismiss();
                Variables.backgroundcolor = "bg7";
                MainActivity.changeBg("bg7");
                helper.updatebg("bg7");
            }
        });
        bg8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cpbg.dismiss();
                Variables.backgroundcolor = "bg8";
                MainActivity.changeBg("bg8");
                helper.updatebg("bg8");
            }
        });
        bg9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cpbg.dismiss();
                Variables.backgroundcolor = "bg9";
                MainActivity.changeBg("bg9");
                helper.updatebg("bg9");
            }
        });
        bg10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cpbg.dismiss();
                Variables.backgroundcolor = "bg10";
                MainActivity.changeBg("bg10");
                helper.updatebg("bg10");
            }
        });

        cpbg = chooseprimarybg.create();
    }


    void CreateView2() {
        view2 = getLayoutInflater().inflate(R.layout.choosetextcolor, null, false);
        choosetextcolor.setView(view2);
        ImageView bg1, bg2, bg3, bg4;
        bg1 = view2.findViewById(R.id.black);
        bg2 = view2.findViewById(R.id.red);
        bg3 = view2.findViewById(R.id.white);
        bg4 = view2.findViewById(R.id.green);


        bg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ctbg.dismiss();
                helper.updatetextbg("black");
            }
        });

        bg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ctbg.dismiss();
                helper.updatetextbg("red");
            }
        });
        bg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ctbg.dismiss();
                helper.updatetextbg("white");
            }
        });
        bg4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ctbg.dismiss();
                helper.updatetextbg("green");
            }
        });


        ctbg = choosetextcolor.create();
    }


    @Override
    public void onBackPressed() {
        finish();

        super.onBackPressed();
    }
}