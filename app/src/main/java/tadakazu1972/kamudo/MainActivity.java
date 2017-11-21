package tadakazu1972.kamudo;

import android.app.Activity;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static java.lang.Integer.parseInt;

public class MainActivity extends Activity implements GLSurfaceView.Renderer {

    final float VIEW_WIDTH = 320;
    final float VIEW_HEIGHT = 480;
    private float lastPointX, lastPointY;
    public int touch_direction;

    private TouchWatcher m_TouchWatcher;
    private TouchManager m_TouchMngr;
    private TouchButton m_Button;

    private GLSurfaceView m_View;
    private DrawDevice m_DrawDevice;
    private AppResource m_AppRes;
    private TextureResource m_TexRes;

    private Model[] m_sMap = new Model[6];
    private Model[] m_sBg = new Model[2];
    public Map m_MAP;
    public float mapx;
    public float mapy;

    protected MyChara m;
    private Model[] m_sKumako = new Model[12];
    protected Shot[] shot = new Shot[16];
    private Model[] m_sShot = new Model[4];
    public static final int KN = 20;
    protected Enemy[] k = new Enemy[KN];
    private Model[] m_sEnemy = new Model[8];
    public int Enemy_number; //かおにゃん登場数
    private Model[] m_sSmallBaloon = new Model[4];
    //星
    public static final int SN = 10;
    protected Star[] s = new Star[SN];
    private Model[] sStar = new Model[7]; //7パターンの絵
    public int star_number;
    public int star_counter;
    //鳥
    public static final int BN = 10;
    protected Bird[] bird = new Bird[BN];
    private Model[] m_sBird = new Model[4];
    public int bird_number;
    //たま
    protected Tama[] t = new Tama[KN];
    private Model[] sTama = new Model[6];
    public static final int CN = 35;
    protected Cake[] c = new Cake[CN];
    protected Baloon b;
    private Model m_sBaloon;
    private Model[] m_sRibbon = new Model[2];
    protected Cloud[] cloud = new Cloud[3];
    private Model[] m_sCloud = new Model[3];
    public static final int IN = 100;
    private Model m_sTitle;
    private Model m_sEndroll;
    //ねこ救出数表示用
    private Model[] m_sNumber = new Model[10];
    private float endY;
    public int counter; //ねこ救出数
    public int stage; //ステージ数
    public int gs; //ゲームステータス
    public int loop; //周回数記憶用
    private SoundPool mSoundPool;
    private int mSoundId1;
    private int mSoundId2;
    private int mSoundId3;
    private int mSoundId4;
    private int mSoundId5;
    private int mSoundId6;
    private MediaPlayer bgmEnding = null;
    //タイマー
    private Timer mainTimer;
    private MainTimerTask mainTimerTask;
    private Handler mHandler = new Handler();

    //タイマータスク派生クラス run()に定周期で処理したい内容を記述
    public class MainTimerTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post (new Runnable() {
                public void run() {
                    if (gs == 1) { //ゲーム中なら以下を実行
                        //ショットが発射されていなかったら発射
                        if (shot[m.shotIndex].visible!=1){
                            shot[m.shotIndex].visible=1;
                            shot[m.shotIndex].base_index = 0;
                            m.shotIndex++;
                            if (m.shotIndex>15) m.shotIndex=0;
                        }
                        //プレイヤーダメージ表現フラグOFF
                        m.damage=0;
                    }
                }
            });
        }
    }

    public MainActivity() {
        super();
        m_View = null;
        m_DrawDevice = null;

        m_TouchWatcher  = null;
        m_TouchMngr     = null;
        m_Button        = null;
        m_AppRes        = null;
        m_TexRes        = null;

        m = null;
        m_MAP = new Map();
        mapx=0.0f;
        mapy=0.0f;
        counter=0;
        stage=1;
        Enemy_number=KN;
        star_number=SN;
        star_counter=0;
        bird_number=0;
        gs=0;
        endY=480.0f;
        loop=0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //タッチ関係初期設定
        lastPointX=0.0f; lastPointY=0.0f;
        touch_direction=0;
        //アーサー生成
        m = new MyChara();
        //ショット生成
        for (int i=0;i<16;i++) shot[i] = new Shot(m);
        //ねこ生成
        for (int i=0;i<KN;i++) k[i]=new Enemy(m_MAP,this);
        //星生成
        for (int t=0;t<SN;t++) s[t]=new Star();
        //鳥生成
        for (int k=0;k<BN;k++) bird[k]=new Bird();
        //たま生成
        for (int i=0;i<KN;i++) t[i]= new Tama(m_MAP,this);
        //バルーン生成
        b = new Baloon();
        //雲生成
        for ( int q=0;q<3;q++) cloud[q]=new Cloud(q);
        m_View = new GLSurfaceView( this );
        m_View.setRenderer( this );
        m_DrawDevice = new DrawDevice();
        m_TouchWatcher  = new TouchWatcher();
        m_TouchMngr     = new TouchManager( m_TouchWatcher );
        m_Button        = new TouchButton( m_TouchMngr );
        {
            m_Button.SetRect( 0, 0, ( int ) DrawDevice.DRAW_WIDTH, ( int ) DrawDevice.DRAW_HEIGHT );
        }
        m_AppRes        = new AppResource( this );
        m_TexRes        = new TextureResource( m_AppRes, m_DrawDevice );
        setContentView( m_View );
        //マップ生成
        loadStage(stage);
        //タイマー
        mainTimer = new Timer();
        mainTimerTask = new MainTimerTask();
        mainTimer.schedule(mainTimerTask, 150, 150);
    }

    @Override
    public void onResume() {
        super.onResume();
        m_View.onResume();
        //音声データ読み込み
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSoundId1 = mSoundPool.load(getApplicationContext(), R.raw.cat1a, 0);
        mSoundId2 = mSoundPool.load(getApplicationContext(), R.raw.get, 0);
        mSoundId3 = mSoundPool.load(getApplicationContext(), R.raw.pote01, 0);
        mSoundId4 = mSoundPool.load(getApplicationContext(), R.raw.clear, 0);
        mSoundId5 = mSoundPool.load(getApplicationContext(), R.raw.piyo, 0);
        mSoundId6 = mSoundPool.load(getApplicationContext(), R.raw.baloon, 0);
        //BGM
        bgmEnding = MediaPlayer.create(getApplicationContext(), R.raw.ending);
        bgmEnding.setLooping(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        m_View.onPause();

        mSoundPool.release();
        bgmEnding.release();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config ) {
        m_DrawDevice.Create(gl);
    }

    @Override
    public void onSurfaceChanged( GL10 gl, int width, int height) {
        Rect recr = new Rect();
        m_View.getWindowVisibleDisplayFrame( recr );

        m_DrawDevice.UpdateDrawArea(gl, width, height, recr.top);

    }

    @Override
    public boolean  onTouchEvent( MotionEvent event )
    {
        final float orgTouchPosX    = event.getX();
        final float orgTouchPosY    = event.getY();
        PointF touchPos        = m_DrawDevice.GetSprite().ScreenPosToDrawPos( new PointF( orgTouchPosX, orgTouchPosY ));

        float touchedX = event.getX();
        float touchedY = event.getY();
        //前回タッチとの差分を算出
        float temp_vx, temp_vy;
        temp_vx = Math.abs(lastPointX-touchedX);
        temp_vy = Math.abs(lastPointY-touchedY);

        TouchWatcher.ACTION action;

        switch( event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                //タイトルのとき
                if (gs==0) {
                    this.PlaySound(2);
                    gs=1;
                }
                action  = TouchWatcher.ACTION.ACTION_DOWN;
                break;
            case MotionEvent.ACTION_MOVE: //タッチされた
                if (temp_vx > temp_vy) {
                    if (lastPointX - touchedX < 0) {
                        touch_direction = 1; //right
                    } else {
                        touch_direction = 2; //left
                    }
                } else {
                    if (lastPointY - touchedY < 0) {
                        touch_direction = 3; //down
                    } else {
                        touch_direction = 4; //up
                    }
                }
                //タッチ座標更新
                lastPointX = touchedX;
                lastPointY = touchedY;
                action  = TouchWatcher.ACTION.ACTION_MOVE;
                break;
            case MotionEvent.ACTION_UP:
                action  = TouchWatcher.ACTION.ACTION_UP;    break;
            default:
                return true;
        }

        if( !m_TouchWatcher.AddAction( action, touchPos ))
        {
            System.err.println( "TouchEventOverFlow!!" );
        }

        return true;
    }

    @Override
    public void onDrawFrame( GL10 gl ) {
        //シーンの更新
        m_DrawDevice.Begin( gl );
        {
            m_TouchMngr.Update();

            //画像読み込み
            if (m_sKumako[0] == null){
                initModel();
                loadImage();
            }
            //描画
            //*****************************************************************************
            //タイトル
            if (gs==0) {
                //bgm
                if (bgmEnding.isPlaying()){
                    bgmEnding.pause();
                    bgmEnding.seekTo(0);
                }
                //背景
                m_sBg[0].Draw(0, 0, m_DrawDevice);
                //雲
                for (int q=0;q<3;q++) {
                    m_sCloud[q].Draw(cloud[q].x,cloud[q].y, m_DrawDevice);
                    cloud[q].Move1(this);
                }
                //タイトル
                m_sTitle.Draw(0, 0, m_DrawDevice);
                //アーサー
                int index = m.base_index + m.index / 10;
                m_sKumako[index].Draw(m.x, m.y, m_DrawDevice);
                //移動
                //アーサー
                m.move(touch_direction, VIEW_WIDTH, VIEW_HEIGHT, m_MAP, this);

                //********************************************************************************
                //通常ゲーム
            } else if (gs==1) {
                //背景
                m_sBg[0].Draw(0, 0, m_DrawDevice);
                //MAP
                for (int i = 0; i < 15; i++) {
                    for (int j = 0; j < 10; j++) {
                        int mapid = m_MAP.MAP[i][j];
                        if (mapid!=0) {
                            m_sMap[mapid].Draw(j * 32 + mapx, i * 32 + mapy, m_DrawDevice);
                        }
                    }
                }
                //バルーン
                m_sBaloon.Draw(b.x, b.y, m_DrawDevice);
                int b_index = b.index / 10;
                m_sRibbon[b_index].Draw(b.x, b.y + 200.0f, m_DrawDevice);
                //ねこ＋小バルーン
                for (int i1 = 0; i1 < Enemy_number; i1++) {
                    Enemy i = k[i1];
                    if (i.visible==1) {
                        int k_index = loop * 4 + i.base_index + i.index / 10;
                        m_sEnemy[k_index].Draw(i.x+mapx, i.y+mapy, m_DrawDevice);
                    }
                }
                //鳥
                for (int k1 = 0; k1 < bird_number; k1++) {
                    Bird k = bird[k1];
                    int bird_index = k.base_index + k.index / 10;
                    m_sBird[bird_index].Draw(k.x, k.y, m_DrawDevice);
                }
                //たま
                for (Tama _tama : t) {
                    int tama_index = _tama.base_index + _tama.index / 10;
                    sTama[tama_index].Draw(_tama.x, _tama.y, m_DrawDevice);
                }
                //ショット
                for (int i=0;i<16;i++) {
                    int shotIndex = shot[i].base_index;
                    m_sShot[shotIndex].Draw(shot[i].x, shot[i].y, m_DrawDevice);
                }
                //くまちゃん
                int index;
                if (m.damage==0) {
                    index = m.base_index + m.index / 10;
                } else {
                    index = m.base_index + m.index / 10; //ダメージ表現スプライトのIndexで8を足しておく
                }
                m_sKumako[index].Draw(m.x, m.y, m_DrawDevice);
                //星
                for (int t1 = 0; t1 < star_number; t1++) {
                    Star i = s[t1];
                    if (i.visible==1) {
                        int k_index = i.index / 10;
                        sStar[k_index].Draw(i.x, i.y, m_DrawDevice);
                    }
                }
                //ねこ救出数
                int n1 = (counter/100)%10;
                int n2 = (counter/10)%10;
                int n3 = counter%10;
                if (counter>99) m_sNumber[n1].Draw(262, 10, m_DrawDevice);
                if (counter>9)  m_sNumber[n2].Draw(278, 10, m_DrawDevice);
                m_sNumber[n3].Draw(294, 10, m_DrawDevice);
                /*//雲
                for (int q=0;q<3;q++) {
                    m_sCloud[q].Draw(cloud[q].x,cloud[q].y, m_DrawDevice);
                    cloud[q].Move1(this);
                }*/
                //**********************************************************************
                //移動
                //くまちゃん
                m.move(touch_direction, VIEW_WIDTH, VIEW_HEIGHT, m_MAP, this);
                //ショット
                for (int i=0;i<16;i++) {
                    shot[i].move(m, this, m_MAP);
                }
                //ねこ+小バルーン
                for (int i2 = 0; i2 < Enemy_number; i2++) {
                    Enemy i = k[i2];
                    if (i.visible==1) {
                        i.move(m, this, VIEW_WIDTH, VIEW_HEIGHT, m_MAP, s[star_counter]);
                    }
                }
                //星
                for (int t2 = 0; t2 < star_number; t2++) {
                    Star i = s[t2];
                    if (i.visible==1) {
                        i.Move();
                    }
                }
                //鳥
                for (int k2 = 0; k2 < bird_number; k2++) {
                    bird[k2].Move(m, this, VIEW_WIDTH, VIEW_HEIGHT, m_MAP);
                }
                //たま
                for ( Tama tamas : t) {
                    tamas.move(m_MAP, this);
                }
                //バルーン
                b.Move(m, this);

                //エンディング
            } else if (gs==2) {
                //bgm
                if (!bgmEnding.isPlaying()){
                    bgmEnding.start();
                }
                //背景
                m_sBg[1].Draw(0, 0, m_DrawDevice);
                //雲
                for (int q = 0; q < 3; q++) {
                    m_sCloud[q].Draw(cloud[q].x, cloud[q].y, m_DrawDevice);
                    cloud[q].Move2(this);
                }
                //バルーン
                m_sBaloon.Draw(b.x, b.y, m_DrawDevice);
                int b_index = b.index / 10;
                m_sRibbon[b_index].Draw(b.x, b.y + 200.0f, m_DrawDevice);
                //リボンのアニメーション処理
                b.index++;
                if (b.index > 19) b.index = 0;
                b.Move(m, this);
                //くまちゃん
                int index = m.base_index + m.index / 10;
                m_sKumako[index].Draw(b.x + 84, b.y + 200, m_DrawDevice);
                //ねこ
                //ねこ＋小バルーン
                for (int i1 = 0; i1 < Enemy_number; i1++) {
                    Enemy i = k[i1];
                    int k_index = i.base_index + i.index / 10;
                    m_sEnemy[k_index].Draw(i.x+mapx, i.y+mapy, m_DrawDevice);
                    i.moveEnding(this, m_MAP);
                }
                //エンドロール
                m_sEndroll.Draw(0, endY, m_DrawDevice);
                endY = endY - 0.6f;
                b.y = b.y - 0.3f; //バルーンの上昇速度はエンドロールより遅め
                if (endY < -1540.0f) {
                    endY = 480.0f;
                    //バルーン画面上の待機位置へ
                    b.setY(-260.0f);
                    stage=0;
                    //鳥初期化
                    for (int i=0;i<bird_number; i++){
                        bird[i].Reset();
                        //bgmストップ
                        bgmEnding.pause();
                        bgmEnding.seekTo(0);
                    }
                    bird_number=0;
                    //ねこ救出数再セット
                    counter=0;
                    gs = 0;
                }
            }
        }
        m_DrawDevice.End();

        //  タッチイベント監視人更新
        m_TouchWatcher.Update();
    }

    public void initModel(){
        for (int i=0; i<m_sKumako.length; i++){
            m_sKumako[i] = new Model();
        }
        for (int i=0; i<m_sShot.length; i++){
            m_sShot[i] = new Model();
        }
        for (int i=0; i<m_sMap.length; i++){
            m_sMap[i] = new Model();
        }
        for (int i=0; i<m_sEnemy.length; i++){
            m_sEnemy[i] = new Model();
        }
        for (int i=0; i<m_sSmallBaloon.length; i++){
            m_sSmallBaloon[i] = new Model();
        }
        for (int i=0; i<sStar.length; i++){
            sStar[i] = new Model();
        }
        for (int i=0; i<m_sBird.length; i++){
            m_sBird[i] = new Model();
        }
        for (int i=0; i<sTama.length; i++){
            sTama[i] = new Model();
        }
        m_sBg[0] = new Model();
        m_sBg[1] = new Model();
        m_sBaloon = new Model();
        m_sRibbon[0] = new Model();
        m_sRibbon[1] = new Model();
        m_sCloud[0] = new Model();
        m_sCloud[1] = new Model();
        m_sCloud[2] = new Model();
        m_sTitle = new Model();
        m_sEndroll = new Model();
        //ねこ救出数表示数字
        for (int j=0; j<m_sNumber.length; j++){
            m_sNumber[j] = new Model();
        }
    }

    public void loadImage(){
        m_sKumako[0].Create("kumako01", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[1].Create("kumako02", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[2].Create("kumako03", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[3].Create("kumako04", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[4].Create("kumako05", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[5].Create("kumako06", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[6].Create("kumako07", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[7].Create("kumako08", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[8].Create("kumakobanzai2", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[9].Create("kumakobanzai2", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[10].Create("kumakobanzai2", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sKumako[11].Create("kumakobanzai2", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sShot[0].Create("baloon01", 0.0f, 0.0f, 0, 0, 16, 16, m_TexRes);
        m_sShot[1].Create("baloon02", 0.0f, 0.0f, 0, 0, 16, 16, m_TexRes);
        m_sShot[2].Create("baloon03", 0.0f, 0.0f, 0, 0, 16, 16, m_TexRes);
        m_sShot[3].Create("baloon04", 0.0f, 0.0f, 0, 0, 16, 16, m_TexRes);
        m_sMap[0].Create("black", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sMap[1].Create("ladder01", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sMap[2].Create("grassblock", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sMap[3].Create("greenblock", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sMap[4].Create("bird01", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sMap[5].Create("redbrick01", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sEnemy[0].Create("neko03", 0.0f, 0.0f, 0, 0, 16, 16, m_TexRes);
        m_sEnemy[1].Create("neko04", 0.0f, 0.0f, 0, 0, 16, 16, m_TexRes);
        m_sEnemy[2].Create("neko01", 0.0f, 0.0f, 0, 0, 16, 16, m_TexRes);
        m_sEnemy[3].Create("neko02", 0.0f, 0.0f, 0, 0, 16, 16, m_TexRes);
        m_sEnemy[4].Create("babyduck03", 0.0f, 0.0f, 0, 0, 16, 16, m_TexRes);
        m_sEnemy[5].Create("babyduck04", 0.0f, 0.0f, 0, 0, 16, 16, m_TexRes);
        m_sEnemy[6].Create("babyduck01", 0.0f, 0.0f, 0, 0, 16, 16, m_TexRes);
        m_sEnemy[7].Create("babyduck02", 0.0f, 0.0f, 0, 0, 16, 16, m_TexRes);
        m_sSmallBaloon[0].Create("baloon01", 0.0f, 0.0f, 0, 0, 16,16, m_TexRes);
        m_sSmallBaloon[1].Create("baloon02", 0.0f, 0.0f, 0, 0, 16,16, m_TexRes);
        m_sSmallBaloon[2].Create("baloon03", 0.0f, 0.0f, 0, 0, 16,16, m_TexRes);
        m_sSmallBaloon[3].Create("baloon04", 0.0f, 0.0f, 0, 0, 16,16, m_TexRes);
        //星
        sStar[0].Create("get01", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        sStar[1].Create("get02", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        sStar[2].Create("get03", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        sStar[3].Create("get04", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        sStar[4].Create("get05", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        sStar[5].Create("get06", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        sStar[6].Create("get07", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        //鳥
        m_sBird[0].Create("bird03", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sBird[1].Create("bird04", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sBird[2].Create("bird01", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        m_sBird[3].Create("bird02", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        //たま
        sTama[0].Create("tama01", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        sTama[1].Create("tama02", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        sTama[2].Create("tama03", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        sTama[3].Create("tama04", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        sTama[4].Create("tama05", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        sTama[5].Create("tama06", 0.0f, 0.0f, 0, 0, 32, 32, m_TexRes);
        //
        m_sBg[0].Create("bg_ending", 0.0f, 0.0f, 0, 0, 320, 480, m_TexRes);
        m_sBg[1].Create("bg_ending", 0.0f, 0.0f, 0, 0, 320, 480, m_TexRes);
        m_sBaloon.Create("bigbaloon", 0.0f, 0.0f, 0, 0, 200, 200, m_TexRes);
        m_sRibbon[0].Create("baloonribbon01", 0.0f, 0.0f, 0, 0, 200, 96, m_TexRes);
        m_sRibbon[1].Create("baloonribbon02", 0.0f, 0.0f, 0, 0, 200, 96, m_TexRes);
        m_sCloud[0].Create("cloud01", 0.0f, 0.0f, 0, 0, 120, 60, m_TexRes);
        m_sCloud[1].Create("cloud02", 0.0f, 0.0f, 0, 0, 120, 60, m_TexRes);
        m_sCloud[2].Create("cloud03", 0.0f, 0.0f, 0, 0, 120, 60, m_TexRes);
        m_sTitle.Create("title", 0.0f, 0.0f, 0, 0, 320, 480, m_TexRes);
        m_sEndroll.Create("endroll", 0.0f, 0.0f, 0, 0, 320, 960, m_TexRes);
        //ねこ救出数表示数字
        for (int j=0;j<10;j++){
            String filename = String.format( "%d", j );
            m_sNumber[j].Create(filename, 0.0f, 0.0f, 0, 0, 16, 16, m_TexRes);
        }
    }

    public void SetTouch() {
        if (touch_direction==1) {
            touch_direction=2;
        } else {
            touch_direction=1;
        }
    }

    public void GotoNextStage() {
        stage++;
        if (stage>10) {
            //バルーンを画面下に配置、上昇するようにvisibleも２にセット
            b.setY(490.0f);
            b.visible=2;
            //ねこをランダム座標にセット
            for (int i=0;i<KN;i++){
                k[i].setEndingPosition();
            }
            gs=2;
            //loopを1にして、ねこをひよこに変更する
            loop=1;
        }
        //鳥初期化
        for (int i=0;i<bird_number; i++){
            bird[i].Reset();
        }
        bird_number=0;
        //マップ再セット
        loadStage(stage);
        //たま初期化
        for (Tama _tama : t){
            _tama.reset(m_MAP);
        }
        //ねこ救出数再セット
        counter=0;
        //くまちゃん再セット
        m.reset();
    }

    public void PlaySound(int id){
        mSoundPool.play(id, 1.0f, 1.0f, 0, 0, 1.0f);
    }

    public void loadCSV(String filename){
        InputStream is = null;
        try {
            try {
                //assetsフォルダ内のcsvファイル読込
                is = getAssets().open(filename);
                InputStreamReader ir = new InputStreamReader(is, "UTF-8");
                CSVReader csvreader = new CSVReader(ir, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 0);//0行目から
                String[] csv;
                int y = 0;
                while((csv = csvreader.readNext()) != null){
                    for (int x=0;x<10;x++){
                        //読み込んだデータをある程度のレンジに変換して格納
                        int data = 0;
                        int _data = parseInt(csv[x]);
                        m_MAP.MAP[y][x] = _data;
                        //鳥についてマップデータ「４」「５」を走査：初期座標、登場数
                        if (_data == 4) {
                            //初期座標セット
                            bird[bird_number].setInitXY(x,y);
                            //登場数増加
                            bird_number++;
                        }
                    }
                    y++; if (y>15){ y=0;}
                }
                //データ代入
            } finally {
                if (is != null) is.close();
                //Toast.makeText(this, "マップデータ読込完了", Toast.LENGTH_SHORT).show();
            }
        } catch(Exception e){
            //Toast.makeText(this, "CSV読込エラー", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadStage(int stage){
        String filename = String.format( "stage%d.csv", stage );
        loadCSV(filename);
    }
}
