package gif;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bing.test2.R;
import com.otaliastudios.gif.GIFCompressor;
import com.otaliastudios.gif.GIFListener;

import java.io.File;
import java.io.IOException;

public class GifToMp4Activity extends AppCompatActivity {

    private File mOutputFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif_to_mp4);
        Button btTransferMp4 = findViewById(R.id.btTransferMp4);
        String inPath = this.getFilesDir().getAbsolutePath()+File.separator+"1.gif";
        try {
            File outputDir = new File(this.getFilesDir().getAbsolutePath(), "outputs");
            //noinspection ResultOfMethodCallIgnored
            outputDir.mkdir();
            mOutputFile = File.createTempFile("transcode_test", ".mp4", outputDir);
        } catch (IOException e) {
            Toast.makeText(this, "Failed to create temporary file.", Toast.LENGTH_LONG).show();
        }

        btTransferMp4.setOnClickListener(v -> {
            GIFCompressor.into(mOutputFile.getPath())
                    .addDataSource(this, inPath)
                    .setListener(new GIFListener() {
                        public void onGIFCompressionProgress(double progress) {
                            Log.d("hanbing", "progress="+progress);
                        }
                        public void onGIFCompressionCompleted() {
                            Log.d("hanbing", "onGIFCompressionCompleted");
                        }
                        public void onGIFCompressionCanceled() {
                            Log.d("hanbing", "onGIFCompressionCanceled");
                        }
                        public void onGIFCompressionFailed(@NonNull Throwable exception) {
                            Log.d("hanbing", "error="+exception.getMessage());
                        }
                    }).compress();
        });


    }
}
