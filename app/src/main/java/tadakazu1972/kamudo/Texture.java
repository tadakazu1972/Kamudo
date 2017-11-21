package tadakazu1972.kamudo;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by tadakazu on 2017/11/20.
 */

public class Texture {
    private DrawDevice m_DrawDevice;
    private int m_TexID;
    private int m_ImageWidth;
    private int m_ImageHeight;

    //コンストラクタ
    protected Texture() {
        _Clear();
    }

    //開放
    public void Release () {
        if (m_DrawDevice != null ) {
            GL10 gl = m_DrawDevice.GetGl();
            int[] aTexID = new int[ 1 ];
            {
                aTexID[ 0 ] = m_TexID;
                gl.glDeleteTextures( 1, aTexID, 0 );
            }
        }
        _Clear();
    }

    //各種取得
    final public boolean IsLoaded() { return ( m_DrawDevice != null ); }
    final public int GetTextureBindID() { return m_TexID; }
    final public int GetWidth() { return m_ImageWidth; }
    final public int GetHeight() { return m_ImageHeight; }

    //ロード
    protected boolean LoadImage(DrawDevice drawDevice, Bitmap bmp ) {
        if ( IsLoaded() ) {
            //ロード済み
            return false;
        }

        GL10 gl = drawDevice.GetGl();

        //テクスチャ作成
        int[] aTexID = new int [1];
        {
            gl.glGenTextures( 1, aTexID, 0 );
        }
        m_TexID = aTexID[0];
        gl.glBindTexture( GL10.GL_TEXTURE_2D, m_TexID );

        //拡大、縮小フィルタ設定
        //gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST );
        gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR );
        gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR );

        //繰り返し方法を設定
        gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE );
        gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE );

        //ポリゴンとテクスチャの色関係設定
        gl.glTexEnvf( GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE );

        //サイズ取得
        m_ImageWidth = bmp.getWidth();
        m_ImageHeight = bmp.getHeight();

        //テクスチャ作成
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
        bmp.recycle();

        //描画装置保持
        m_DrawDevice = drawDevice;

        return true;
    }

    //クリア
    private void _Clear() {
        m_DrawDevice = null;
        m_TexID = -1;
        m_ImageWidth = 0;
        m_ImageHeight = 0;
    }
}
