package tadakazu1972.kamudo;

/**
 * Created by tadakazu on 2017/11/21.
 */

public class Star {
    public float x, y;
    public int index;
    public int visible;

    public Star(){
        x = 0.0f;
        y = 0.0f;
        index = 0;
        visible = 0;
    }

    public void Reset(){
        x = 0.0f;
        y = 0.0f;
        index = 0;
        visible = 0;
    }

    public void Set(float x1, float y1){
        x = x1;
        y = y1;
        index = 0;
        visible = 1;
    }

    public void Move(){
        //アニメーションインデックス変更処理
        index=index+6;
        if ( index > 69 ) {
            //消去というか初期化
            Reset();
        }
    }
}
