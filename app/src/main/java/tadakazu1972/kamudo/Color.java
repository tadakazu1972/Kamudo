package tadakazu1972.kamudo;

/**
 * Created by tadakazu on 2017/11/20.
 */

public class Color {
    private float   m_R;
    private float   m_G;
    private float   m_B;
    private float   m_A;

    //  デフォルトカラー取得
    public static Color Black() { return new Color( 0.0f, 0.0f, 0.0f, 1.0f );   }
    public static Color White() { return new Color( 1.0f, 1.0f, 1.0f, 1.0f );   }
    public static Color Red()   { return new Color( 1.0f, 0.0f, 0.0f, 1.0f );   }

    //  コンストラクタ
    public Color()
    {
        SetColor( 0.0f, 0.0f, 0.0f, 0.0f );
    }
    public Color( float r, float g, float b, float a )
    {
        SetColor( r, g, b, a );
    }

    //  色設定
    public void SetColor( float r, float g, float b, float a )
    {
        m_R = r;
        m_G = g;
        m_B = b;
        m_A = a;
    }

    //  コピー
    public void set( Color rhs )
    {
        m_R = rhs.m_R;
        m_G = rhs.m_G;
        m_B = rhs.m_B;
        m_A = rhs.m_A;
    }

    //  掛け算
    public void Mul( Color rhs )
    {
        m_R *= rhs.m_R;
        m_G *= rhs.m_G;
        m_B *= rhs.m_B;
        m_A *= rhs.m_A;
    }

    //  比較
    public boolean  equals( Object rhs )
    {
        final Color color   = ( Color )rhs;

        return (( m_R == color.m_R ) && ( m_G == color.m_G ) && ( m_B == color.m_B ) && ( m_A == color.m_A ));
    }

    //  各色取得
    public float    GetR()  { return m_R;   }
    public float    GetG()  { return m_G;   }
    public float    GetB()  { return m_B;   }
    public float    GetA()  { return m_A;   }
}
