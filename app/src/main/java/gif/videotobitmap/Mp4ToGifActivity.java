package gif.videotobitmap;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bing.test2.R;

import java.io.File;

import gif.gifencoder.GifExtractor;


public class Mp4ToGifActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp4_to_gif);
        String path = this.getFilesDir().getAbsolutePath()+File.separator+"test.mp4";
        File file = new File(getFilesDir().getAbsolutePath() + "/gif/test.gif");
        if(file.exists()){
            file.delete();
        }
        GifExtractor gifExtractor = new GifExtractor(this, path);
        gifExtractor.encoder(file.getPath(), 0, 10 * 1000, 15,15, 320, 240);
    }

}
