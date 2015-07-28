package bg.ittalents.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import bg.ittalents.game.Resource.Assets;
import bg.ittalents.game.Resource.Constant;

/**
 * Created by Ob1 on 7/4/2015.
 */
public class GameWinScreen implements Screen{
    private static final int CONSTANT_SEE_SCREEN = 3;
    private static final int CONSTANT_UNLOCKED_WEAPON_2 = 25000;
    private static final int CONSTANT_UNLOCKED_WEAPON_3 = 35000;
    private static final float X_FOR_YOU_WIN = Constant.WIDTH_SCREEN / 3.1f;
    private static final int Y_FOR_YOU_WIN = Constant.HEIGHT_SCREEN / 2 + Gdx.graphics.getHeight() / 3;
    private static final float X_FOR_SAVE_TEXT = Constant.WIDTH_SCREEN / 7.5f;
    private static final int Y_FOR_SAVE_TEXT = Constant.HEIGHT_SCREEN / 2 + Gdx.graphics.getHeight() / 7;
    private static final float X_FOR_SCORE = Constant.WIDTH_SCREEN / 3.15f;
    private static final int CONSTANT_FOR_Y_SCORE = 2;

    private Game game;
    private SpriteBatch batch;
    private Sprite backgroundSprite;
    private Stage stage;
    private float minSeeScreen;
    private BitmapFont textBitmapFont;


    protected GameWinScreen(Game game){
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        backgroundSprite = new Sprite(Assets.gameWinScreen);
        backgroundSprite.setSize(Constant.WIDTH_SCREEN, Constant.HEIGHT_SCREEN);
        stage = new Stage(new ScreenViewport());

        Assets.gamePlayMusic.stop();
        Assets.gameWinMusic.play();

        Gdx.input.setInputProcessor(stage);
        textBitmapFont = loadFont();
        stage.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (minSeeScreen > CONSTANT_SEE_SCREEN) {
                    Assets.gameWinMusic.stop();
                    game.setScreen(new PlayScreen(game));
                }
                return true;
            }
        });

        if(!LoginScreen.offlineModeSelect) {
            postJsonScore();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        backgroundSprite.draw(batch);
        batch.draw(backgroundSprite, backgroundSprite.getX(), backgroundSprite.getY(), backgroundSprite.getWidth(), backgroundSprite.getHeight());
        textBitmapFont.draw(batch, "You Win !", X_FOR_YOU_WIN, Y_FOR_YOU_WIN);
        textBitmapFont.draw(batch, "You Saved The Camp !", X_FOR_SAVE_TEXT, Y_FOR_SAVE_TEXT);
        textBitmapFont.draw(batch, "SCORE  " + GameScreen.points, X_FOR_SCORE, Gdx.graphics.getHeight() / CONSTANT_FOR_Y_SCORE - textBitmapFont.getLineHeight() / CONSTANT_FOR_Y_SCORE);
        batch.end();
        setTimeSeeScreen(Gdx.graphics.getDeltaTime());
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    private void setTimeSeeScreen(float delta){
        minSeeScreen += delta;
    }

    public BitmapFont loadFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("game-font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = Gdx.graphics.getHeight() / 8;

        BitmapFont defaultFont = generator.generateFont(fontParameter);

        defaultFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        return defaultFont;
    }

    private void postJsonScore() {
        JsonObject json = new JsonObject();
        json.add("userId", new JsonPrimitive(User.getSingletonUser().getUserId()));
        json.add("level", new JsonPrimitive(User.getSingletonUser().getGameLevel()));
        json.add("score", new JsonPrimitive(GameScreen.points));
        final Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
        httpRequest.setUrl(Assets.HTTP_SERVER + "levelManager");
        httpRequest.setHeader("Content-Type", "application/json");
        httpRequest.setContent(json.toString());
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(final Net.HttpResponse httpResponse) {
                if (httpResponse.getStatus().getStatusCode() == 200) {
                    System.out.println(httpResponse.getResultAsString());
                    if (((User.getSingletonUser().getScore() + GameScreen.points) > CONSTANT_UNLOCKED_WEAPON_2) && (User.getSingletonUser().getWeaponTwoUnlock() == 0)) {
                        postJsonWeapons(2);
                    }
                    if (((User.getSingletonUser().getScore() + GameScreen.points) > CONSTANT_UNLOCKED_WEAPON_3) && (User.getSingletonUser().getWeaponTreeUnlock() == 0)) {
                        postJsonWeapons(3);
                    }
                }
            }

            @Override
            public void failed(Throwable t) {
                LoginScreen.labelMessage.setText("Please check your Internet connection.");
                game.setScreen(new LoginScreen(game));
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
//                     ?????
                    }
                });
            }
        });
    }

    private void postJsonWeapons(int weapont) {
        JsonObject json = new JsonObject();
        json.add("userId", new JsonPrimitive(User.getSingletonUser().getUserId()));
        json.add("weaponType", new JsonPrimitive(weapont));
        final Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
        httpRequest.setUrl(Assets.HTTP_SERVER + "weaponsStore");
        httpRequest.setHeader("Content-Type", "application/json");
        httpRequest.setContent(json.toString());
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(final Net.HttpResponse httpResponse) {
                if (httpResponse.getStatus().getStatusCode() == 200) {
                }
            }

            @Override
            public void failed(Throwable t) {
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
//                     ?????
                    }
                });
            }
        });
    }


    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        game.dispose();
        batch.dispose();
        stage.dispose();
        textBitmapFont.dispose();
    }
}
