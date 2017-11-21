package tadakazu1972.kamudo;

import android.graphics.PointF;
import android.graphics.Rect;

/**
 * Created by tadakazu on 2017/11/20.
 */

public class Model {

    //  コンストラクタ
    public  Model()
    {
        super();
        m_Image         = null;
        m_ImageCenter   = null;
        m_ImageRect     = null;
    }

    private Texture m_Image;
    private PointF m_ImageCenter;
    private Rect m_ImageRect;

    //  作成
    public void Create( String imageName, float cx, float cy, int left, int top, int right, int bottom, TextureResource texRes )
    {
        m_Image         = texRes.Load( imageName );
        m_ImageCenter   = new PointF( cx, cy );
        m_ImageRect     = new Rect( left, top, right, bottom );
    }

    //  描画
    public void Draw( float posX, float posY, DrawDevice drawDevice )
    {
        Sprite sprite      = drawDevice.GetSprite();
        Sprite.DrawParam drawParam   = sprite.CreateDrawParam( m_Image );
        {
            drawParam.m_Pos.set( posX, posY );
            drawParam.m_ImageCenter.set( m_ImageCenter );
            drawParam.m_ImageRect.set( m_ImageRect );
        }
        sprite.Draw( drawParam );
    }
}
