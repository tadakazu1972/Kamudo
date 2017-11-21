package tadakazu1972.kamudo;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by tadakazu on 2017/11/20.
 */

public class DrawDevice {
    private GL10 m_Gl;
    private Sprite m_Sprite;
    //仮想描画領域サイズ
    public final static float DRAW_WIDTH = 320.0f;
    public final static float DRAW_HEIGHT = 480.0f;

    //constructor
    public DrawDevice() {
        m_Gl = null;
        m_Sprite = null;
    }

    //create
    public void Create( GL10 gl ) {

        //スプライト設定
        m_Sprite = new Sprite( this );

        //ディザを無効化
        gl.glDisable( GL10.GL_DITHER );

        //カラーとテクスチャ座標の補間制度を最も効率的なものにする
        gl.glHint( GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST );

        //バッファクリア時のカラー情報をセット
        gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f );

        //カリング無効化
        gl.glDisable( GL10.GL_CULL_FACE );

        //深度テスト無効化
        gl.glDisable( GL10.GL_DEPTH_TEST );

        //アルファ有効化
        gl.glEnable( GL10.GL_BLEND );
        gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );

        //スムースシェーディングモード
        gl.glShadeModel( GL10.GL_SMOOTH);
    }

    //描画サイズ更新
    public void UpdateDrawArea( GL10 gl, int width, int height, int statusBarHeight ) {
        m_Sprite.UpdateScreenMatrix( gl, width, height, statusBarHeight );
    }

    //描画開始
    public void Begin ( GL10 gl ) {
        //クリア
        gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
        m_Gl = gl;
        m_Sprite.Begin();
    }

    //描画終了
    public void End() {
        m_Sprite.End();
        m_Gl = null;
    }

    //いろいろ取得
    public Sprite GetSprite() { return m_Sprite; }
    public GL10 GetGl() { return m_Gl; }
}
