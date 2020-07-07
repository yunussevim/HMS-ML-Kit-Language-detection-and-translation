package com.langtranslation.huawei;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.langdetect.MLLangDetectorFactory;
import com.huawei.hms.mlsdk.langdetect.local.MLLocalLangDetector;
import com.huawei.hms.mlsdk.langdetect.local.MLLocalLangDetectorSetting;
import com.huawei.hms.mlsdk.model.download.MLLocalModelManager;
import com.huawei.hms.mlsdk.model.download.MLModelDownloadStrategy;
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting;
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslator;
import com.huawei.hms.mlsdk.translate.local.MLLocalTranslateSetting;
import com.huawei.hms.mlsdk.translate.local.MLLocalTranslator;
import com.huawei.hms.mlsdk.translate.local.MLLocalTranslatorModel;

public class MainActivity extends AppCompatActivity {
    MLLocalLangDetector mlLocalLangDetector;
    Button translateButton;
    TextView detectedLangText;
    TextView translatedText;
    EditText sourceText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        translateButton = findViewById(R.id.translateButton);
        detectedLangText = findViewById(R.id.detectedText);
        translatedText = findViewById(R.id.translatedText);
        sourceText = findViewById(R.id.sourceText);

        MLLangDetectorFactory factory = MLLangDetectorFactory.getInstance();
        MLLocalLangDetectorSetting setting = new MLLocalLangDetectorSetting.Factory()

                .setTrustedThreshold(0.01f)
                .create();
        mlLocalLangDetector = factory.getLocalLangDetector(setting);

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = sourceText.getText().toString();
                detectLanguage(text);
            }
        });


    }

    public void detectLanguage(final String source_Text) {
        Task<String> firstBestDetectTask = mlLocalLangDetector.firstBestDetect(source_Text);
        firstBestDetectTask.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                detectedLangText.setText(s);
                translate(source_Text,s);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {

            }
        });
        if (mlLocalLangDetector != null) {
            mlLocalLangDetector.stop();
        }


    }
    public void translate(String sourceText, String sourceLangCode ){
        MLRemoteTranslateSetting setting = new MLRemoteTranslateSetting
                .Factory()
                .setSourceLangCode(sourceLangCode)
                .setTargetLangCode("en")
                .create();
        MLRemoteTranslator mlRemoteTranslator = MLTranslatorFactory.getInstance().getRemoteTranslator(setting);

        final Task<String> task = mlRemoteTranslator .asyncTranslate(sourceText);
        task.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String text) {
                translatedText.setText(text);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                try {
                    MLException mlException = (MLException)e;
                    int errorCode = mlException.getErrCode();
                    String errorMessage = mlException.getMessage();
                } catch (Exception error) {

                }
            }
        });
        if (mlRemoteTranslator!= null) {
            mlRemoteTranslator.stop();
        }

    }



}