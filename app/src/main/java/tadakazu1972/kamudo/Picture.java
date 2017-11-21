package tadakazu1972.kamudo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

/**
 * Created by tadakazu on 2017/11/20.
 */

public class Picture extends Texture {
    public Picture() {
        super();
    }

    //読み込み
    public boolean Load(DrawDevice drawDevice, InputStream inputStream ) {
        if ( IsLoaded() )
            return false;

        Bitmap bmp = BitmapFactory.decodeStream(inputStream);

        return LoadImage( drawDevice, bmp );
    }
}
