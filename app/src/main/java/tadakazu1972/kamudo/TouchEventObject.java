package tadakazu1972.kamudo;

import android.graphics.PointF;
import android.graphics.Rect;

/**
 * Created by tadakazu on 2017/11/20.
 */

public abstract class TouchEventObject {
    private Rect m_Rect;
    //  コンストラクタ
    protected TouchEventObject( TouchManager mngr )
    {
        m_Rect  = new Rect();

        //  管理人に登録
        mngr.AddObject( this );
    }
    //  更新
    public abstract void    Update();

    //  通知
    //  通知座標はオブジェの相対座標
    public abstract void    OnTouchUp( PointF pos );
    public abstract void    OnTouchDown( PointF pos );
    public abstract void    OnTouchMove( PointF pos );
    public abstract void    OnTouchOut( PointF pos );

    //  領域設定
    public void SetRect( int posX, int posY, int width, int height )
    {
        m_Rect.left     = posX;
        m_Rect.top      = posY;
        m_Rect.right    = posX + width;
        m_Rect.bottom   = posY + height;
    }

    //  各種取得
    public Rect GetRect()   { return m_Rect;    }
    public boolean  IsEnable()  { return true;      }
}
