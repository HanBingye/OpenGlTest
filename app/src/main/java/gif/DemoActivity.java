package gif;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bing.test2.R;
import com.otaliastudios.gif.GIFCompressor;
import com.otaliastudios.gif.GIFListener;

import java.io.File;
import java.io.IOException;

import gif.gifencoder.GifExtractor;
import gif.util.GifUtils;

public class DemoActivity extends AppCompatActivity {
    private File outputMp4File;
    private File outputGifFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        Bitmap bitmap = GifUtils.getBitmapArrayByGif(this, "ori.gif", 10);
        findViewById(R.id.btToMp4).setOnClickListener(v -> {
            String inPath = this.getFilesDir().getAbsolutePath() + File.separator + "ori.gif";
            try {
                File outputDir = new File(this.getFilesDir().getAbsolutePath(), "mp4");
                if (!outputDir.exists()) {
                    outputDir.mkdir();
                }
                outputMp4File = File.createTempFile("mp4_", ".mp4", outputDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
            GIFCompressor.into(outputMp4File.getPath()).addDataSource(this, inPath).setListener(new GIFListener() {
                public void onGIFCompressionProgress(double progress) {
                    Log.d("hanbing", "progress=" + progress);
                }

                public void onGIFCompressionCompleted() {
                    Log.d("hanbing", "onGIFCompressionCompleted");
                    Toast.makeText(DemoActivity.this, "转mp4成功", Toast.LENGTH_SHORT).show();
                }

                public void onGIFCompressionCanceled() {
                    Log.d("hanbing", "onGIFCompressionCanceled");
                }

                public void onGIFCompressionFailed(@NonNull Throwable exception) {
                    Log.d("hanbing", "error=" + exception.getMessage());
                }
            }).compress();

        });
        findViewById(R.id.btToGif).setOnClickListener(v -> {
            String path = this.getFilesDir().getAbsolutePath() + File.separator + "test.mp4";
            try {
                File outputDir = new File(this.getFilesDir().getAbsolutePath(), "gif");
                if (!outputDir.exists()) {
                    outputDir.mkdir();
                }
                outputGifFile = File.createTempFile("gif_", ".gif", outputDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
            GifExtractor gifExtractor = new GifExtractor(this, path);
            gifExtractor.encoder(outputGifFile.getPath(), 0, 10 * 1000, 15, 15, 500, 500);
            gifExtractor.setMp4Listener(new GifExtractor.Mp4Listener() {
                @Override
                public void onComplete() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DemoActivity.this, "转gif成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        });

    }
}
