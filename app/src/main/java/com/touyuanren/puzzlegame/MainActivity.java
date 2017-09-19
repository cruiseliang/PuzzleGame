package com.touyuanren.puzzlegame;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
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
    //当前手势
    private GestureDetector mDetctor;
    //记录游戏是否开始
    private boolean isStart = false;
    //记录动画是否正在执行
    private boolean isAnimRun = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetctor.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mDetctor.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetctor = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                int direction = detectDicr(motionEvent.getX(), motionEvent.getY(), motionEvent1.getX(), motionEvent1.getY());
                moveBoxByGesture(direction);
                return false;
            }
        });
        setContentView(R.layout.activity_main);
        init();

    }

    public void init() {
        gl_mian_game = (GridLayout) findViewById(R.id.gl_main_game);
        //获取一张图片转换为bitmap
        Bitmap bm = ((BitmapDrawable) (getResources().getDrawable(R.mipmap.jiansan))).getBitmap();
        //如果切割为正方形，则需要切割的图片宽高比为5:3
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
                        boolean bl = isadjacent((ImageView) view);
                        if (bl) {
                            changeDataByImageView((ImageView) view);
                        }
//                        Toast.makeText(MainActivity.this, "d" + bl, Toast.LENGTH_LONG).show();
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
        setRandom();
    }

    //设置一个空方块
    public void setNullImage(ImageView imageView) {
        imageView.setImageBitmap(null);
        GameData mGameData = (GameData) imageView.getTag();
        mGameData.bitmap = null;
        ig_null = imageView;
    }

    //随机打乱方块的顺序
    private void setRandom() {
        //通过循环来多次调用依据手势移动方块的方法来随机打乱方块的顺序
        for (int i = 0; i < 2; i++) {
            int flag = (int) ((Math.random() * 4) + 1);
            //Log.e(TAG, "setRandom: flag---"+flag );
            //需要移动时没有动画效果
            moveBoxByGesture(flag, false);
        }
        isStart = true;
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
//            Toast.makeText(MainActivity.this, "上", Toast.LENGTH_LONG).show();
            return true;
        } else if (gameData.y == mNullGameData.y && gameData.x == mNullGameData.x + 1) {//当前点击位置在空方块下方
//            Toast.makeText(MainActivity.this, "下", Toast.LENGTH_LONG).show();
            return true;
        } else if (gameData.x == mNullGameData.x && gameData.y == mNullGameData.y - 1) {//当前点击位置在空方块左方
//            Toast.makeText(MainActivity.this, "左", Toast.LENGTH_LONG).show();
            return true;
        } else if (gameData.x == mNullGameData.x && gameData.y == mNullGameData.y + 1) {//当前点击位置在空方块右方
//            Toast.makeText(MainActivity.this, "右", Toast.LENGTH_LONG).show();
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

        //通过比较某一方块初始位置与当前位置的关系，判断该方块是否在其初始位置
        public boolean isTrue() {
            if (x == p_x && y == p_y) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void changeDataByImageView(final ImageView imageView) {
        if (isAnimRun) {
            return;
        }
        //创建位移动画，进行交换
        TranslateAnimation animation = null;
        if (imageView.getX() > ig_null.getX()) {
            //右边，往左移动
            animation = new TranslateAnimation(0.1f, -imageView.getWidth(), 0.1f, 0.1f);
        } else if (imageView.getX() < ig_null.getX()) {
            animation = new TranslateAnimation(0.1f, imageView.getWidth(), 0.1f, 0.1f);
        } else if (imageView.getY() > ig_null.getY()) {
            //往左移动
            animation = new TranslateAnimation(0.1f, 0.1f, 0.1f, -imageView.getWidth());
        } else if (imageView.getY() < ig_null.getY()) {
            //往下移动
            animation = new TranslateAnimation(0.1f, 0.1f, 0.1f, imageView.getWidth());
        }
        animation.setDuration(70);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimRun = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimRun = false;
                imageView.clearAnimation();
                changeLoaction(imageView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animation);
    }

    //交换位置
    public void changeLoaction(ImageView imageView) {

        GameData gameData = (GameData) imageView.getTag();
        ig_null.setImageBitmap(gameData.bitmap);
        GameData mNullGameData = (GameData) ig_null.getTag();
        mNullGameData.bitmap = gameData.bitmap;
        mNullGameData.p_x = gameData.p_x;
        mNullGameData.p_y = gameData.p_y;
        setNullImage(imageView);
        //每次交换完成后都进行判断游戏是否结束
        if (isStart) {
            isGameOver();
        }
    }

    /**
     * 判断手势的方向
     *
     * @param start_x 起始的x位置
     * @param start_y 起始的y位置
     * @param end_x
     * @param end_y
     * @return 1, 2, 3, 4对应上下左右
     */
    private int detectDicr(float start_x, float start_y, float end_x, float end_y) {
        boolean isLeftOrRight = Math.abs(start_x - end_x) > Math.abs(start_y - end_y) ? true : false;
        if (isLeftOrRight) {

            if (start_x - end_x > 0) {
                return 3;
            } else if (start_x - end_x < 0) {
                return 4;
            }
        } else {
            if (start_y - end_y > 0) {
                return 1;
            } else if (start_y - end_y < 0) {
                return 2;
            }
        }
        return 0;
    }

    //通过手势来移动方块：1,2,3,4对应上下左右
    private void moveBoxByGesture(int ges) {
        moveBoxByGesture(ges, true);
    }

    private void moveBoxByGesture(int ges, boolean isAnime) {
        GameData null_box = (GameData) ig_null.getTag();
        int new_x = null_box.x;
        int new_y = null_box.y;
        //new_x是自定义的下标，指的是二维数组（元素是图片小方块）的坐标（不是某个方块的xy坐标），所以上下是由x控制。
        if (ges == 1) {
            new_x++;
        } else if (ges == 2) {
            new_x--;
        } else if (ges == 3) {
            new_y++;
        } else if (ges == 4) {
            new_y--;
        }
        if (new_x >= 0 && new_x < iv_game_arr.length && new_y >= 0 && new_y < iv_game_arr[0].length) {
            if (isAnime) {
                changeDataByImageView(iv_game_arr[new_x][new_y]);
            } else if (!isAnime) {
                changeLoaction(iv_game_arr[new_x][new_y]);
            }
        }
    }

    //判读游戏是否结束，通过循环遍历所有方块当前位置与初始位置是否相等来判读
    private void isGameOver() {
        boolean isOver = false;
        boolean isBreakFromj = false;
        for (int i = 0; i < iv_game_arr.length; i++) {
            if (isBreakFromj) {
                break;
            }
            for (int j = 0; j < iv_game_arr[0].length; j++) {
                //若是空方块，则跳过
                if (iv_game_arr[i][j] == ig_null) {
                    continue;
                }
                GameData box = (GameData) iv_game_arr[i][j].getTag();
                if (box.isTrue()) {
                    isOver = true;
                } else if (!box.isTrue()) {
                    isOver = false;
                    //break只能跳出一层循环
                    isBreakFromj = true;
                    break;
                }
            }
        }
        if (isOver) {
            Toast.makeText(getApplicationContext(), "congratulations game over!", Toast.LENGTH_SHORT).show();
        }
    }
}
