package tadakazu1972.kamudo;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.opengl.GLU;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by tadakazu on 2017/11/20.
 */

public class Sprite {
    final static int    VERTEX_NUM              = 4;
    final static int    VERTEX_POS_ELEMENT_NUM  = 2;
    final static int    IMAGE_POS_ELEMENT_NUM   = 2;
    final static int    VERTEX_COLOR_ELEMET_NUM = 4;
    private DrawDevice m_DrawDevice;
    private BufferSet   m_VertexPos;
    private BufferSet   m_ImageRect;
    private BufferSet   m_VertexColor;
    private float       m_DrawScaleRate;
    private int     m_StatusBarHeight;
    private Point m_DrawOffset;
    private PointF m_CameraModeOffset;
    private Color m_ScaleColor;
    private Color m_DrawColor;
    private boolean m_bScreenMode;
    private Color m_PrevDrawColor;
    //  描画パラメータクラス
    public class DrawParam
    {
        public DrawParam( Texture image )
        {
            m_Image         = image;
            m_Pos           = new PointF();
            m_RotationZ     = 0.0f;
            m_Scale         = new PointF( 1.0f, 1.0f );
            m_ImageRect     = new Rect();
            m_ImageCenter   = new PointF();
            m_Color         = Color.White();
        }

        public Texture m_Image;
        public PointF   m_Pos;
        public float    m_RotationZ;
        public PointF   m_Scale;
        public Rect     m_ImageRect;
        public PointF   m_ImageCenter;
        public Color m_Color;
    }

    //  コンストラクタ
    public Sprite( DrawDevice drawDevice )
    {
        m_DrawDevice    = drawDevice;

        //  頂点情報バッファ作成
        {
            m_VertexPos     = new BufferSet();
            m_ImageRect     = new BufferSet();
            m_VertexColor   = new BufferSet();
            {
                m_VertexPos.Create( VERTEX_NUM * VERTEX_POS_ELEMENT_NUM );
                m_ImageRect.Create( VERTEX_NUM * IMAGE_POS_ELEMENT_NUM );
                m_VertexColor.Create( VERTEX_NUM * VERTEX_COLOR_ELEMET_NUM );
            }
        }

        m_DrawScaleRate     = 1.0f;
        m_StatusBarHeight   = 0;
        m_DrawOffset        = new Point( 0, 0 );
        m_CameraModeOffset  = new PointF( 0.0f, 0.0f );
        m_ScaleColor        = Color.White();
        m_DrawColor         = Color.White();
        m_bScreenMode       = true;

        m_PrevDrawColor     = new Color( 0.0f, 0.0f, 0.0f, 0.0f );

        SetLookPos( new PointF( 0.0f, 0.0f ));
    }

    //  スクリーン変換行列更新
    public void UpdateScreenMatrix(GL10 gl, int width, int height, int statusBarHeight )
    {
        final float drawWidth   = DrawDevice.DRAW_WIDTH;
        final float drawHeight  = DrawDevice.DRAW_HEIGHT;
        {
            //  拡大率更新
            final float widthScale  = width / drawWidth;
            final float heightScale = height / drawHeight;

            m_DrawScaleRate = Math.min( widthScale, heightScale );
        }

        final int       viewWidth   = ( int )( drawWidth * m_DrawScaleRate );
        final int       viewHeight  = ( int )( drawHeight * m_DrawScaleRate );
        {
            //  描画オフセット更新
            m_DrawOffset.x  = ( int )( width * 0.5f - viewWidth * 0.5f );
            m_DrawOffset.y  = ( int )( height * 0.5f - viewHeight * 0.5f );
        }

        gl.glViewport( m_DrawOffset.x, m_DrawOffset.y, viewWidth, viewHeight );
        //gl.glScissor( m_DrawOffset.x, m_DrawOffset.y, viewWidth, viewHeight );
        //gl.glEnable( GL10.GL_SCISSOR_TEST );

        gl.glMatrixMode( GL10.GL_PROJECTION );
        gl.glLoadIdentity();
        gl.glOrthof( 0.0f, drawWidth, drawHeight, 0.0f, 1.0f, 10.0f );

        m_StatusBarHeight   = statusBarHeight;
    }

    //  スクリーン座標を描画座標に変換
    public PointF   ScreenPosToDrawPos( final PointF pos )
    {
        PointF  newPos  = new PointF( pos.x, pos.y );
        {
            newPos.x    -= m_DrawOffset.x;
            newPos.y    -= m_DrawOffset.y + m_StatusBarHeight;

            newPos.x    /= m_DrawScaleRate;
            newPos.y    /= m_DrawScaleRate;
        }
        return newPos;
    }

    //  見ている位置設定
    public void SetLookPos( PointF lookPos )
    {
        m_CameraModeOffset.x    = -lookPos.x    + DrawDevice.DRAW_WIDTH * 0.5f;
        m_CameraModeOffset.y    = lookPos.y     + DrawDevice.DRAW_HEIGHT * 0.5f;
    }

    //  スケールカラー設定
    public void SetScaleColor( Color color )
    {
        m_ScaleColor.set( color );
    }

    //  スクリーンモード設定
    public void SetScreenMode( boolean bScreenMode )
    {
        m_bScreenMode   = bScreenMode;
    }

    //  描画開始
    public void Begin()
    {
        GL10    gl  = m_DrawDevice.GetGl();

        //  テクスチャ有効化
        gl.glActiveTexture( GL10.GL_TEXTURE0 );

        //  カメラ座標設定
        GLU.gluLookAt(gl, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

        //  頂点情報有効化
        gl.glEnableClientState( GL10.GL_VERTEX_ARRAY );
        gl.glEnableClientState( GL10.GL_COLOR_ARRAY );

        //  デフォルトはスクリーンモード
        SetScreenMode( true );
    }

    //  描画終了
    public void End()
    {
    }

    //  描画パラメータ作成
    public DrawParam CreateDrawParam( Texture image )
    {
        return new DrawParam( image );
    }
    public DrawParam CreateDrawParamNoTexture()
    {
        return new DrawParam( null );
    }

    //  描画
    public void Draw( DrawParam param )
    {
        if( !_IsDrawEnable( param ))
            return;

        GL10    gl  = m_DrawDevice.GetGl();

        final Texture image       = param.m_Image;
        final Rect      imageRect   = param.m_ImageRect;
        final PointF    imageCenter = param.m_ImageCenter;
        final Color     color       = param.m_Color;
        final float imageWidth  = image.GetWidth();
        final float imageHeight = image.GetHeight();

        //  表示用行列設定
        _UpdateWorldMatrix( gl, param );

        //  テクスチャ設定
        gl.glEnable( GL10.GL_TEXTURE_2D );
        gl.glBindTexture( GL10.GL_TEXTURE_2D, param.m_Image.GetTextureBindID());

        //  頂点の位置設定
        {
            final float leftPos     = -imageCenter.x;
            final float topPos      = -imageCenter.y;
            final float rightPos    = leftPos + imageRect.width();
            final float bottomPos   = topPos + imageRect.height();
            final float aVerticalPos[]  =
                    {
                            leftPos,    topPos,
                            rightPos,   topPos,
                            leftPos,    bottomPos,
                            rightPos,   bottomPos,
                    };

            m_VertexPos.SetArray( aVerticalPos );
            gl.glVertexPointer( VERTEX_POS_ELEMENT_NUM, GL10.GL_FLOAT, 0, m_VertexPos.GetFloatBuffer());
        }

        //  テクスチャ領域設定
        {
            final float imageWidthInv   = 1.0f / imageWidth;
            final float imageHeightInv  = 1.0f / imageHeight;
            final float leftImagePos    = ( imageRect.left      + 0.5f )    * imageWidthInv;
            final float topImagePos     = ( imageRect.top       + 0.5f )    * imageHeightInv;
            final float rightImagePos   = ( imageRect.right     - 0.5f )    * imageWidthInv;
            final float bottomImagePos  = ( imageRect.bottom    - 0.5f )    * imageHeightInv;
            final float aImageRect[]    =
                    {
                            leftImagePos,   topImagePos,
                            rightImagePos,  topImagePos,
                            leftImagePos,   bottomImagePos,
                            rightImagePos,  bottomImagePos,
                    };

            //  テクスチャ座標有効化
            gl.glEnableClientState( GL10.GL_TEXTURE_COORD_ARRAY );

            m_ImageRect.SetArray( aImageRect );
            gl.glTexCoordPointer( IMAGE_POS_ELEMENT_NUM, GL10.GL_FLOAT, 0, m_ImageRect.GetFloatBuffer());
        }

        //  色設定
        m_DrawColor.set( m_ScaleColor );
        m_DrawColor.Mul( color );
        if( !m_PrevDrawColor.equals( m_DrawColor ))
        {
            final float colorR      = m_DrawColor.GetR();
            final float colorG      = m_DrawColor.GetG();
            final float colorB      = m_DrawColor.GetB();
            final float colorA      = m_DrawColor.GetA();
            final float aColor[]    =
                    {
                            colorR, colorG, colorB, colorA,
                            colorR, colorG, colorB, colorA,
                            colorR, colorG, colorB, colorA,
                            colorR, colorG, colorB, colorA,
                    };

            m_VertexColor.SetArray( aColor );
            gl.glColorPointer( VERTEX_COLOR_ELEMET_NUM, GL10.GL_FLOAT, 0, m_VertexColor.GetFloatBuffer());

            m_PrevDrawColor.set( m_DrawColor );
        }

        //  描画
        gl.glDrawArrays( GL10.GL_TRIANGLE_STRIP, 0, VERTEX_NUM );
    }

    //  バッファセット
    private class BufferSet
    {
        //  コンストラクタ
        public BufferSet()
        {
            m_FloatBuffer   = null;
        }

        //  作成
        public void Create( int arrayNum )
        {
            ByteBuffer byteBuffer  = ByteBuffer.allocateDirect( arrayNum * 4 );
            {
                byteBuffer.order( ByteOrder.nativeOrder());
            }
            m_FloatBuffer   = byteBuffer.asFloatBuffer();
        }

        //  配列設定
        public void SetArray( float[] aValue )
        {
            m_FloatBuffer.put( aValue );
            m_FloatBuffer.position( 0 );
        }

        //  Floatバッファ取得
        public FloatBuffer GetFloatBuffer()
        {
            return m_FloatBuffer;
        }

        private FloatBuffer m_FloatBuffer;
    }

    //  ワールドマトリクス更新
    private final void  _UpdateWorldMatrix( GL10 gl, DrawParam param )
    {
        PointF  pos     = param.m_Pos;
        PointF  scale   = param.m_Scale;

        if( !m_bScreenMode )
        {
            //  注視点設定モードのときは座標変更
            pos.x   = pos.x + m_CameraModeOffset.x;
            pos.y   = -pos.y + m_CameraModeOffset.y;
        }

        gl.glMatrixMode( GL10.GL_MODELVIEW );
        gl.glLoadIdentity();

        gl.glTranslatef( pos.x, pos.y, 0.0f );
        gl.glRotatef( param.m_RotationZ, 0.0f, 0.0f, 1.0f );
        gl.glScalef( scale.x, scale.y, 0.0f );
    }

    //  描画する意味があるか？
    private final boolean   _IsDrawEnable( DrawParam param )
    {
        //  アルファが0なら描画する意味無し
        if( param.m_Color.GetA() == 0.0f )
            return false;

        final Rect      imageRect   = param.m_ImageRect;
        final PointF    imageCenter = param.m_ImageCenter;
        final float imageLeft   = -imageCenter.x;
        final float imageTop    = -imageCenter.y;
        final float imageRight  = imageRect.width()     - imageCenter.x;
        final float imageBottom = imageRect.height()    - imageCenter.y;

        final PointF    scale   = param.m_Scale;
        final float rotZ    = param.m_RotationZ;
        final PointF    pos     = param.m_Pos;

        //  モデルの位置計算
        float   drawLeftTopX        = imageLeft;
        float   drawLeftTopY        = imageTop;
        float   drawLeftBottomX     = imageLeft;
        float   drawLeftBottomY     = imageBottom;
        float   drawRightTopX       = imageRight;
        float   drawRightTopY       = imageTop;
        float   drawRightBottomX    = imageRight;
        float   drawRightBottomY    = imageBottom;
        {
            //  拡大率
            {
                drawLeftTopX        *= scale.x;
                drawLeftTopY        *= scale.y;
                drawLeftBottomX     *= scale.x;
                drawLeftBottomY     *= scale.y;
                drawRightTopX       *= scale.x;
                drawRightTopY       *= scale.y;
                drawRightBottomX    *= scale.x;
                drawRightBottomY    *= scale.y;
            }

            //  Z軸回転
            if( rotZ != 0.0f )
            {
                final float rad = ( float )Math.toRadians( rotZ );
                final float sin = ( float )Math.sin( rad );
                final float cos = ( float )Math.cos( rad );

                drawLeftTopX        = drawLeftTopX      * cos - drawLeftTopY        * sin;
                drawLeftTopY        = drawLeftTopX      * sin + drawLeftTopY        * cos;
                drawLeftBottomX     = drawLeftBottomX   * cos - drawLeftBottomY     * sin;
                drawLeftBottomY     = drawLeftBottomX   * sin + drawLeftBottomY     * cos;
                drawRightTopX       = drawRightTopX     * cos - drawRightTopY       * sin;
                drawRightTopY       = drawRightTopX     * sin + drawRightTopY       * cos;
                drawRightBottomX    = drawRightBottomX  * cos - drawRightBottomY    * sin;
                drawRightBottomY    = drawRightBottomX  * sin + drawRightBottomY    * cos;
            }

            //  位置計算
            {
                final float posX    = pos.x;
                final float posY    = (( m_bScreenMode )? pos.y : -pos.y );

                drawLeftTopX        += posX;
                drawLeftTopY        += posY;
                drawLeftBottomX     += posX;
                drawLeftBottomY     += posY;
                drawRightTopX       += posX;
                drawRightTopY       += posY;
                drawRightBottomX    += posX;
                drawRightBottomY    += posY;
            }
        }

        //  描画位置計算
        //  カメラモードのときはカメラ位置の分だけオフセットがある
        if( !m_bScreenMode )
        {
            drawLeftTopX        += m_CameraModeOffset.x;
            drawLeftTopY        += m_CameraModeOffset.y;
            drawLeftBottomX     += m_CameraModeOffset.x;
            drawLeftBottomY     += m_CameraModeOffset.y;
            drawRightTopX       += m_CameraModeOffset.x;
            drawRightTopY       += m_CameraModeOffset.y;
            drawRightBottomX    += m_CameraModeOffset.x;
            drawRightBottomY    += m_CameraModeOffset.y;
        }

        //  上下左右の領域計算
        final float left    = Math.min( Math.min( Math.min( drawLeftTopX, drawLeftBottomX ), drawRightTopX ), drawRightBottomX );
        final float top     = Math.min( Math.min( Math.min( drawLeftTopY, drawLeftBottomY ), drawRightTopY ), drawRightBottomY );
        final float right   = Math.max( Math.max( Math.max( drawLeftTopX, drawLeftBottomX ), drawRightTopX ), drawRightBottomX );
        final float bottom  = Math.max( Math.max( Math.max( drawLeftTopY, drawLeftBottomY ), drawRightTopY ), drawRightBottomY );

        //  描画範囲外なら描画する意味無し
        if(( left > DrawDevice.DRAW_WIDTH ) || ( top > DrawDevice.DRAW_HEIGHT ) || ( right < 0.0f ) || ( bottom < 0.0f ))
            return false;

        return true;
    }
}
