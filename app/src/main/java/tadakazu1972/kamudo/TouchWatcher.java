package tadakazu1972.kamudo;

import android.graphics.PointF;

import java.util.Vector;

/**
 * Created by tadakazu on 2017/11/20.
 */

public class TouchWatcher {
    private ACTION  m_Action;
    private PointF m_Pos;
    private static int  MAX_ACTION_INFO_NUM = 32;
    private Vector< ActionInfo > m_ActionVec;
    private int                 m_DeleteNum;

    //  アクション
    public enum ACTION
    {
        ACTION_DOWN,
        ACTION_UP,
        ACTION_MOVE,
    }

    //  コンストラクタ
    public  TouchWatcher()
    {
        m_ActionVec = new Vector< ActionInfo >( MAX_ACTION_INFO_NUM );
        m_DeleteNum = 0;
    }

    //  アクション追加
    public boolean  AddAction( ACTION action, PointF pos )
    {
        ActionInfo  info    = new ActionInfo( action, pos );
        return m_ActionVec.add( info );
    }

    //  アクション削除
    public void DeleteAction( int num )
    {
        m_DeleteNum = num;
    }

    //  更新
    public void Update()
    {
        for( int index = 0; index < m_DeleteNum; ++index )
        {
            m_ActionVec.remove( 0 );
        }
        m_DeleteNum = 0;
    }

    //  アクション情報取得
    public int      GetActionNum()              { return m_ActionVec.size();                    }
    public ACTION   GetAction( int index )      { return m_ActionVec.get( index ).GetAction();  }
    public PointF GetPosition( int index )    { return m_ActionVec.get( index ).GetPos();     }

    //  アクション情報
    private class ActionInfo
    {
        //  コンストラクタ
        public ActionInfo( ACTION action, PointF pos )
        {
            m_Action    = action;
            m_Pos       = pos;
        }

        //  各種取得
        public ACTION   GetAction() { return m_Action;  }
        public PointF   GetPos()    { return m_Pos;     }
    }
}
