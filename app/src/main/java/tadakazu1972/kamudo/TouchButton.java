package tadakazu1972.kamudo;

import android.graphics.PointF;

/**
 * Created by tadakazu on 2017/11/20.
 */

public class TouchButton extends TouchEventObject {
    private PointF m_TouchPos;

    //  コンストラクタ
    public  TouchButton( TouchManager mngr )
    {
        super( mngr );

        m_TouchPos  = new PointF();
    }

    //  触れた場所を取得
    public PointF   GetTouchPos()
    {
        return m_TouchPos;
    }

    //  更新
    @Override
    public void Update()
    {
    }

    //  指が離れた
    @Override
    public void OnTouchUp( PointF pos )
    {
    }

    //  指がそっと触れた（激しいかもしれない）
    @Override
    public void OnTouchDown( PointF pos )
    {
    }

    //  指が動いた
    @Override
    public void OnTouchMove( PointF pos )
    {
        //  移動した位置を記憶
        m_TouchPos.set( pos );
    }

    //  指がどこかへ去っていった
    @Override
    public void OnTouchOut( PointF pos )
    {
    }
}
