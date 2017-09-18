package com.touyuanren.puzzlegame;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ImageView[][] iv_game_arr = new ImageView[3][5];//利用二维数组创建若干个小方块
    private GridLayout gl_mian_game;//游戏主界面
    //当前空方块位置
    private ImageView ig_null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gl_mian_game = (GridLayout) findViewById(R.id.gl_main_game);
        //获取一张大图
        Bitmap bm = ((BitmapDrawable) (getResources().getDrawable(R.mipmap.jiansan))).getBitmap();
        int picWidth = bm.getWidth() / 5;//宽和高
        for (int i = 0; i < iv_game_arr.length; i++) {
            for (int j = 0; j < iv_game_arr[0].length; j++) {
                //切割大图片为15份
                Bitmap bitmap = Bitmap.createBitmap(bm, j * picWidth, i * picWidth, picWidth, picWidth);
                iv_game_arr[i][j] = new ImageView(this);
                iv_game_arr[i][j].setImageBitmap(bitmap);
                iv_game_arr[i][j].setPadding(2, 2, 2, 2);
                iv_game_arr[i][j].setTag(new GameData(i, j, bitmap));
                iv_game_arr[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean  bl=isadjacent((ImageView) view);
                        if (bl){
                            changeDataByImageView((ImageView) view);
                        }
                        Toast.makeText(MainActivity.this,"d"+bl,Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        for (int i = 0; i < iv_game_arr.length; i++) {
            for (int j = 0; j < iv_game_arr[0].length; j++) {
                gl_mian_game.addView(iv_game_arr[i][j]);
            }
        }
        setNullImage(iv_game_arr[2][4]);
    }

    //设置一个空方块
    public void setNullImage(ImageView imageView) {
        imageView.setImageBitmap(null);
        ig_null = imageView;
    }

    /**
     * 判断当前点击的方块与空方块位置相邻
     *
     * @param imageView ：点击的方块
     * @return true :相邻 false  :不相邻
     */
    public boolean isadjacent(ImageView imageView) {
        //空方块的位置
        GameData mNullGameData = (GameData) ig_null.getTag();
        //当前点击的位置
        GameData gameData = (GameData) imageView.getTag();
        if (gameData.y == mNullGameData.y && gameData.x == mNullGameData.x - 1) {//当前点击位置在空方块上方
            return true;
        } else if (gameData.y == mNullGameData.y && gameData.x == mNullGameData.x + 1) {//当前点击位置在空方块下方
            return true;
        } else if (gameData.x == mNullGameData.x && gameData.y == mNullGameData.y- 1) {//当前点击位置在空方块左方
            return true;
        } else if (gameData.x == mNullGameData.x && gameData.y == mNullGameData.y +1) {//当前点击位置在空方块右方
            return true;
        }
        return false;
    }

    /**
     * 每个小方块上绑定的数据
     */
    class GameData {
        //实际位置x 为行的位置
        public int x;
        //实际位置y  为列的位置
        public int y;
        //交换后的位置
        public int p_x;
        //交换和的位置
        public int p_y;
        //小方块图片
        public Bitmap bitmap;

        public GameData(int x, int y, Bitmap bitmap) {
            this.x = x;
            this.y = y;
            this.p_x = x;
            this.p_y = y;
            this.bitmap = bitmap;
        }
    }

    public  void  changeDataByImageView(final ImageView  imageView){
        //创建位移动画，进行交换
        TranslateAnimation  animation=null;
        if (imageView.getX()>ig_null.getX()){
            //右边，往左移动
            Log.e("dd",imageView.getX()+"//"+ig_null.getX());
            animation=new TranslateAnimation(0.1f,-imageView.getWidth(),0.1f,0.1f);

        }else if(imageView.getX()<ig_null.getX()){
            Log.e("dd",imageView.getX()+"//"+ig_null.getX());
            animation=new TranslateAnimation(0.1f,imageView.getWidth(),0.1f,0.1f);
        }else if(imageView.getY()>ig_null.getY()){
         //往左移动
            Log.e("dd",imageView.getY()+"//"+ig_null.getY());
            animation=new TranslateAnimation(0.1f,0.1f,0.1f,-imageView.getWidth());
        }else if(imageView.getY()<ig_null.getY()){
            Log.e("dd",imageView.getY()+"//"+ig_null.getY()+"////"+imageView.getWidth());
            //往下移动
            animation=new TranslateAnimation(0.1f,0.1f,0.1f,imageView.getWidth());
        }
        animation.setDuration(70);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                imageView.clearAnimation();
                GameData  gameData= (GameData) imageView.getTag();
                ig_null.setImageBitmap(gameData.bitmap);
                GameData  mNullGameData= (GameData) ig_null.getTag();
                mNullGameData.bitmap=gameData.bitmap;
                mNullGameData.p_x=gameData.p_x;
                mNullGameData.p_y=gameData.p_y;
                setNullImage(imageView);
            }
        });
        imageView.startAnimation(animation);
    }
}
