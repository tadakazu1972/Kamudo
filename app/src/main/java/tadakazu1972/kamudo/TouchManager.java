package tadakazu1972.kamudo;

import android.graphics.PointF;
import android.graphics.Rect;

import java.util.Vector;

/**
 * Created by tadakazu on 2017/11/20.
 */

public class TouchManager {
    private TouchWatcher m_Watcher;
    private Vector<TouchEventObject> m_ObjVec;
    private TouchEventObject m_LastActionObj;

    //  コンストラクタ
    public  TouchManager( TouchWatcher watcher )
    {
        m_Watcher   = watcher;

        m_ObjVec        = new Vector<TouchEventObject>();
        m_LastActionObj = null;
    }

    //  更新
    public void Update()
    {
        //  全オブジェクトを更新
        for( int objIndex = 0; objIndex < m_ObjVec.size(); ++objIndex )
        {
            m_ObjVec.get( objIndex ).Update();
        }

        final int   actionNum   = m_Watcher.GetActionNum();
        for( int index = 0; index < actionNum; ++index )
        {
            final PointF pos         = m_Watcher.GetPosition( index );
            TouchEventObject actionObj   = _GetActionObject( pos );

            if(( m_LastActionObj != null ) && ( m_LastActionObj != actionObj ))
            {
                //  オブジェクトが変わっていたら出て行った通知を送る
                final Rect objRect     = m_LastActionObj.GetRect();
                final PointF    localPos    = new PointF( pos.x - objRect.left, pos.y - objRect.top );

                m_LastActionObj.OnTouchOut( localPos );

                m_LastActionObj = actionObj;
            }

            if( actionObj == null )
                continue;

            //  オブジェクトにイベント送信
            {
                //  ローカル位置取得
                final Rect      objRect     = actionObj.GetRect();
                final PointF    localPos    = new PointF( pos.x - objRect.left, pos.y - objRect.top );

                switch( m_Watcher.GetAction( index ))
                {
                    case ACTION_DOWN:   actionObj.OnTouchDown( localPos );  m_LastActionObj = actionObj;    break;
                    case ACTION_UP:     actionObj.OnTouchUp( localPos );    m_LastActionObj = null;         break;
                    case ACTION_MOVE:   actionObj.OnTouchMove( localPos );  m_LastActionObj = actionObj;    break;
                }
            }
        }
        m_Watcher.DeleteAction( actionNum );
    }

    //  オブジェクト追加
    public void AddObject( TouchEventObject obj )
    {
        m_ObjVec.add( obj );
    }

    //  削除
    public void Delete()
    {
        m_ObjVec.clear();
    }

    //  アクションを起こしたオブジェクト取得
    private TouchEventObject _GetActionObject(final PointF pos )
    {
        for( int objIndex = m_ObjVec.size() - 1; objIndex >= 0; --objIndex )
        {
            TouchEventObject obj     = m_ObjVec.get( objIndex );
            if( !obj.IsEnable())
                continue;

            final Rect objRect = obj.GetRect();
            if(( objRect.left <= pos.x ) && ( objRect.top <= pos.y ) && ( objRect.right >= pos.x ) && ( objRect.bottom >= pos.y ))
            {
                return obj;
            }
        }
        return null;
    }
}
