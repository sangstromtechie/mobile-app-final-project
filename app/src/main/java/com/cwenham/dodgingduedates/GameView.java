package com.cwenham.dodgingduedates;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class GameView extends View {

    //Canvas
    private int canvasWidth, canvasHeight;

    //Player
    private Bitmap player;
    private int playerX = 10;
    private int playerY;
    private int playerSpeed;

    //Yellow Ball
    private int yellowX;
    private int yellowY;
    private Paint yellowPaint = new Paint();

    //Black Ball
    private List<BlackBall> blackBallList = new ArrayList<>();

    //Background
    private Bitmap bgImage;

    //Score
    private Paint scorePaint = new Paint();
    private int score;
    private ManageScore manageScore;
    private int highScore;

    //Level
    private Paint levelPaint = new Paint();

    //Life
    private Bitmap life[] = new Bitmap[2];
    private int life_count;

    //Status Check
    private boolean touch_flg = false;
    private int gameState;
    private static final int GAME_START = 0;
    private static final int GAME_PLAY = 1;
    private static final int GAME_OVER = 2;

    //Start & Game Over
    private Bitmap startImage;
    private Bitmap gameOverImage;
    private Bitmap startBtn;
    private Bitmap returnBtn;
    private int btnImageX;
    private int btnImageY;
    private Paint titleScorePaint = new Paint();

    //Sound
    SoundPlayer soundPlayer;

    //Get values from integers.xml
    private int YELLOW_SPEED = getResources().getInteger(R.integer.yellow_speed);
    private int YELLOW_SIZE = getResources().getInteger(R.integer.yellow_size);
    private int BLACK_SPEED = getResources().getInteger(R.integer.black_speed);
    private int BLACK_SIZE = getResources().getInteger(R.integer.black_size);
    private int PLAYER_TAP_SPEED = getResources().getInteger(R.integer.player_tap_speed);
    private int PLAYER_FALL_SPEED = getResources().getInteger(R.integer.player_fall_speed);
    private int SCORE_X_POSITION = getResources().getInteger(R.integer.score_x_position);
    private int SCORE_Y_POSITION = getResources().getInteger(R.integer.score_y_position);
    private int LEVEL_Y_POSITION = getResources().getInteger(R.integer.level_y_position);
    private int LIFE_Y_POSITION = getResources().getInteger(R.integer.life_y_position);

    public GameView(Context context) {
        super(context);

        player = BitmapFactory.decodeResource(getResources(), R.drawable.player);

        startImage = BitmapFactory.decodeResource(getResources(), R.drawable.start);
        bgImage = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        gameOverImage = BitmapFactory.decodeResource(getResources(), R.drawable.gameover);


        yellowPaint.setColor(Color.YELLOW);
        yellowPaint.setAntiAlias(false);

        scorePaint.setColor(Color.BLACK);
        scorePaint.setTextSize(getResources().getInteger(R.integer.score_paint_text_size));
        scorePaint.setTypeface(Typeface.DEFAULT_BOLD);
        scorePaint.setAntiAlias(true);

        levelPaint.setColor(Color.DKGRAY);
        levelPaint.setTextSize(getResources().getInteger(R.integer.level_paint_text_size));
        levelPaint.setTypeface(Typeface.DEFAULT_BOLD);
        levelPaint.setTextAlign(Paint.Align.CENTER);
        levelPaint.setAntiAlias(true);

        life[0] = BitmapFactory.decodeResource(getResources(), R.drawable.heart);
        life[1] = BitmapFactory.decodeResource(getResources(), R.drawable.heart_g);

        startBtn = BitmapFactory.decodeResource(getResources(), R.drawable.start_btn);
        returnBtn = BitmapFactory.decodeResource(getResources(), R.drawable.return_btn);

        titleScorePaint.setTextSize(getResources().getInteger(R.integer.title_score_paint_text_size));
        titleScorePaint.setTypeface(Typeface.DEFAULT_BOLD);
        titleScorePaint.setTextAlign(Paint.Align.CENTER);

        gameState = GAME_START;

        //Sound
        soundPlayer = new SoundPlayer(context);

        //Score
        manageScore = new ManageScore(context);
        highScore = manageScore.loadHighScore();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();

        btnImageX = canvasWidth / 2 - startBtn.getWidth() / 2;
        btnImageY = canvasHeight / 2 - (int)(startBtn.getHeight() * 1.5);

        switch (gameState) {
            case GAME_START:
                canvas.drawBitmap(startImage, 0, 0, null);
                drawStart(canvas);
                break;
            case GAME_PLAY:
                canvas.drawBitmap(bgImage, 0, 0, null);
                drawPlay(canvas);
                break;
            case GAME_OVER:
                canvas.drawBitmap(gameOverImage, 0, 0, null);
                drawOver(canvas);
                break;
        }

    }

    public void drawPlay(Canvas canvas) {
        //Player
        int minPlayerY = player.getHeight();
        int maxPlayerY = canvasHeight - player.getHeight() * 3;
        playerY += playerSpeed;
        if(playerY < minPlayerY)
            playerY = minPlayerY;

        if(playerY > maxPlayerY)
            playerY = maxPlayerY;

        playerSpeed += PLAYER_FALL_SPEED;

        if(touch_flg) {
            canvas.drawBitmap(player, playerX, playerY, null);
            touch_flg = false;
        } else {
            canvas.drawBitmap(player, playerX, playerY, null);
        }

        //Yellow
        yellowX -= YELLOW_SPEED;
        if(hitCheck(yellowX, yellowY)) {
            score += 10;
            yellowX = -100;
            soundPlayer.playGetPointSound();
        }
        if(yellowX < 0) {
            yellowX = canvasWidth + 20;
            yellowY = (int) Math.floor(Math.random() * (maxPlayerY - minPlayerY)) + minPlayerY;
        }
        canvas.drawCircle(yellowX, yellowY, YELLOW_SIZE, yellowPaint);

        //Level
        int level = (int)Math.floor(score / 50) + 1;

        //Add Black Ball
        if(blackBallList.size() < level) {
            for (int i = blackBallList.size(); i < level; i++) {
                int x = canvasWidth + 200;
                int y = (int) Math.floor(Math.random() * (maxPlayerY - minPlayerY)) + minPlayerY;
                BlackBall blackBall = new BlackBall(x, y);
                blackBallList.add(blackBall);
            }
        }

        for(int i = 0; i < blackBallList.size(); i++) {
            BlackBall blackBall = blackBallList.get(i);
            blackBall.move(BLACK_SPEED);

            if(hitCheck(blackBall.getXPos(), blackBall.getYPos())) {
                life_count--;
                soundPlayer.playHitBlackSound();
                if(life_count == 0) {
                    //Game Over
                    if(score > highScore) {
                        //Save score
                        manageScore.saveScore(score);
                        highScore = score;
                    }
                    soundPlayer.pauseBGM();
                    gameState = GAME_OVER;
                    return;
                }
                //Remove from List
                blackBallList.remove(i);
                i--;
                continue;
            }

            if (blackBall.getXPos() < 0) {
                blackBallList.remove(i);
                i--;
                continue;
            }

            blackBall.draw(canvas, BLACK_SIZE);
        }

        //Score
        canvas.drawText("Score : " + score, SCORE_X_POSITION, SCORE_Y_POSITION, scorePaint);

        //Level
        canvas.drawText("Lv." + level, canvasWidth / 2, LEVEL_Y_POSITION, levelPaint);

        //Life
        for(int i = 0; i < 3; i++) {
            int x = (int) (canvasWidth*0.7 + life[0].getWidth() * 1.4 * i);
            int y = LIFE_Y_POSITION;

            if(i < life_count) {
                canvas.drawBitmap(life[0], x, y, null);
            } else {
                canvas.drawBitmap(life[1], x, y, null);
            }
        }
    }

    public void drawStart(Canvas canvas) {
        //Initialize position, score & life
        playerY = canvasHeight / 2;
        yellowX = -100;
        blackBallList.clear();
        score = 0;
        life_count = 3;

        canvas.drawText("High Score : " + highScore, canvasWidth / 2, canvasHeight / 2, titleScorePaint);
        canvas.drawBitmap(startBtn, btnImageX, btnImageY, null);
    }

    public void drawOver(Canvas canvas) {
        canvas.drawText("Score : " + score, canvasWidth / 2, canvasHeight / 2, titleScorePaint);
        canvas.drawBitmap(returnBtn, btnImageX, btnImageY, null);
    }

    public boolean hitCheck(int x, int y) {
        if(playerX < x && x < (playerX + player.getWidth()) &&
                playerY < y && y < (playerY + player.getHeight())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (gameState) {
                case GAME_START:
                    if(buttonTapCheck(startBtn, (int)event.getX(), (int)event.getY())) {
                        soundPlayer.seekToTop();
                        soundPlayer.playBGM();
                        gameState = GAME_PLAY;
                    }
                    break;
                case GAME_PLAY:
                    touch_flg = true;
                    playerSpeed = PLAYER_TAP_SPEED;
                    break;
                case GAME_OVER:
                    if(buttonTapCheck(returnBtn, (int)event.getX(), (int)event.getY())) {
                        gameState = GAME_START;
                    }
                    break;
            }
        }
        return true;
    }

    public boolean buttonTapCheck(Bitmap button, int x, int y) {
        if(btnImageX < x && x < btnImageX + button.getWidth() &&
                btnImageY < y && y < btnImageY + button.getHeight()) {
            return true;
        }
        return false;
    }

    public int getGameState() {
        return gameState;
    }
}
