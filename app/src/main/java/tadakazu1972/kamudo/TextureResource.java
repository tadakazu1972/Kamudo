package tadakazu1972.kamudo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by tadakazu on 2017/11/20.
 */

public class TextureResource {
    private AppResource m_AppRes;
    private DrawDevice m_DrawDevice;
    private Vector< TextureInfo > m_LoadTexInfoVec;

    //  コンストラクタ
    public  TextureResource(AppResource appRes, DrawDevice drawDevice )
    {
        m_AppRes        = appRes;
        m_DrawDevice    = drawDevice;
        m_LoadTexInfoVec    = new Vector< TextureInfo >();
    }

    //  ロード
    public Texture Load(String fileName )
    {
        TextureInfo info    = new TextureInfo( fileName );
        Texture tex     = _GetTexture( info );

        //  すでに読み込まれていたらそれを返す
        if( tex == null )
        {
            //  新しく作ってそれを返す
            //  ファイルパス作成
            String      filePath    = String.format( "png/%s.png", fileName );
            InputStream inputStream = m_AppRes.Load( filePath );
            if( inputStream == null )
                return null;

            Picture picture = new Picture();
            {
                if( picture.Load( m_DrawDevice, inputStream ))
                {
                    //  ロードに成功！
                    tex = picture;
                    info.SetTexture( tex );
                    m_LoadTexInfoVec.add( info );
                }
                else
                {
                    //  ロードに失敗...
                }

                //  ストリームを閉じる
                try
                {
                    inputStream.close();
                }
                catch( IOException e )
                {
                    e.printStackTrace();
                }
            }
        }
        return tex;
    }

    //  ロード済みテクスチャ情報
    private class TextureInfo
    {
        //  コンストラクタ
        public  TextureInfo( String fileName )
        {
            m_FileName  = fileName;
            m_Tex       = null;
        }

        //  テクスチャ設定
        public void SetTexture( Texture tex )
        {
            m_Tex   = tex;
        }

        public Texture GetTexture()
        {
            return m_Tex;
        }

        //  比較
        public boolean  Equal( TextureInfo rhs )
        {
            return ( m_FileName.equals( rhs.m_FileName ));
        }

        private String          m_FileName;
        private Texture m_Tex;
    }

    //  読み込み済みテクスチャ取得
    private Texture _GetTexture(TextureInfo texInfo )
    {
        for(Iterator< TextureInfo > it = m_LoadTexInfoVec.iterator(); it.hasNext(); )
        {
            TextureInfo info    = it.next();
            if( texInfo.Equal( info ))
                return info.GetTexture();
        }
        return null;
    }
}
