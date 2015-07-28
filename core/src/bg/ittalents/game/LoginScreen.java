package bg.ittalents.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import bg.ittalents.game.Resource.Assets;
import bg.ittalents.game.Resource.Constant;

public class LoginScreen implements Screen {
    protected static boolean offlineModeSelect;
    private static final float CONSTANT_PAD_BOTTOM = Gdx.graphics.getHeight() / 30;
    private static boolean counterForStartMusicOneTime;

    private Skin skin;
    private Stage stage;
    private Game zombieShooterGame;
    private Table container;
    private Table buttonsContainer;
    private ImageButton loginButton;
    private ImageButton registerButton;
    private TextField loginField;
    private TextField passwordField;
    private SpriteBatch batch;
    private Sprite backgroundSprite;
    private Sprite zombieShooterSpriteTitle;
    private Sprite spriteRegisterButton;
    private Sprite spriteLoginButton;
    private Sprite offlineModeSprite;
    private SpriteDrawable zombieShooterSpriteDrawable;
    private SpriteDrawable spriteDrawableRegisterButton;
    private SpriteDrawable spriteDrawableLoginButton;
    private SpriteDrawable offlineModeSpriteDrawable;
    private Image imageTitle;
    private Image offlineModeImage;
    protected static Label labelMessage;
    private Table tableMessage;
    private String currentColor;


    protected LoginScreen(Game game) {
        zombieShooterGame = game;
    }

    @Override
    public void show() {
        if (!counterForStartMusicOneTime) {
            gameMenuMusic();
            counterForStartMusicOneTime = true;
        }
        spriteDrawableCreator(); // Creating the image title, register and login buttons

        this.currentColor = "red";
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        stage = new Stage(new ScreenViewport());
        container = new Table();
        buttonsContainer = new Table();


        loginField = new TextField(Constant.USER_NAME, skin);
        passwordField = new TextField(Constant.PASSWORD, skin);
        loginField.setAlignment(Align.center);
        passwordField.setAlignment(Align.center);
        loginField.setColor(1, 0, 0, 0.5f);
        passwordField.setColor(1, 0, 0, 0.5f);


        loginButton = new ImageButton(spriteDrawableRegisterButton);
        registerButton = new ImageButton(spriteDrawableLoginButton);

        clickListenerHandler();

        initializationContainer();

        Gdx.input.setInputProcessor(stage); // This is needed to set up the stage so it can receive inputs from our users.

        batch = new SpriteBatch();
        backgroundSprite = new Sprite(Assets.backgroundMenu);
        backgroundSprite.setSize(Constant.WIDTH_SCREEN, Constant.HEIGHT_SCREEN);


        initializationWarningMessage();
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        backgroundSprite.draw(batch);
        batch.draw(backgroundSprite, backgroundSprite.getX(), backgroundSprite.getY(), backgroundSprite.getWidth(), backgroundSprite.getHeight());
        batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    private void spriteDrawableCreator() { // Creating the image title, register and login buttons
        zombieShooterSpriteTitle = new Sprite(Assets.zombieShooterTitle);
        Assets.spriteDefaultColorSolid(zombieShooterSpriteTitle);
        zombieShooterSpriteDrawable = new SpriteDrawable(zombieShooterSpriteTitle);
        imageTitle = new Image(zombieShooterSpriteDrawable);

        spriteRegisterButton = new Sprite(Assets.loginButton);
        Assets.spriteDefaultColor(spriteRegisterButton);
        spriteRegisterButton.setSize(Constant.CONSTANT_WIDTH_FIELD_AND_BUTTON, Constant.CONSTANCE_HEIGHT_BUTTONS);
        spriteDrawableRegisterButton = new SpriteDrawable(spriteRegisterButton);

        spriteLoginButton = new Sprite(Assets.registerButton);
        Assets.spriteDefaultColor(spriteLoginButton);
        spriteLoginButton.setSize(Constant.CONSTANT_WIDTH_FIELD_AND_BUTTON, Constant.CONSTANCE_HEIGHT_BUTTONS);
        spriteDrawableLoginButton = new SpriteDrawable(spriteLoginButton);

        offlineModeSprite = new Sprite(Assets.offlineModeRedImage);
        Assets.spriteDefaultColor(offlineModeSprite);
        offlineModeSpriteDrawable = new SpriteDrawable(offlineModeSprite);
        offlineModeImage = new Image(offlineModeSpriteDrawable);

    }

    private void initializationContainer() { // Arranging the screen and how all the things are sorted in it.

        container.clear();
        container.setWidth(stage.getWidth());
        container.align(Align.center | Align.top);
        container.setPosition(0, Constant.HEIGHT_SCREEN);

        container.add(imageTitle).width(Constant.WIDTH_SCREEN).height(Constant.CONSTANT_HEIGHT_TITLE).padBottom(CONSTANT_PAD_BOTTOM);
        container.row();
        container.add(loginField).width(Constant.CONSTANT_WIDTH_FIELD_AND_BUTTON).height(Constant.CONSTANT_HEIGHT_FIELD_AND_BUTTON).padBottom(CONSTANT_PAD_BOTTOM);
        container.row();
        container.add(passwordField).width(Constant.CONSTANT_WIDTH_FIELD_AND_BUTTON).height(Constant.CONSTANT_HEIGHT_FIELD_AND_BUTTON).padBottom(CONSTANT_PAD_BOTTOM);
        container.row();
        container.add(offlineModeImage).width(Constant.CONSTANT_WIDTH_FIELD_AND_BUTTON).height(Constant.CONSTANT_HEIGHT_FIELD_AND_BUTTON).padBottom(CONSTANT_PAD_BOTTOM);
        container.row();
        buttonsContainer.add(loginButton).padRight(Constant.CONSTANT_PAD_LEFT_AND_RIGHT);
        buttonsContainer.add(registerButton).padLeft(Constant.CONSTANT_PAD_LEFT_AND_RIGHT);
        container.add(buttonsContainer);
        stage.addActor(container);
    }

    private void initializationWarningMessage() {
        tableMessage = new Table();
        tableMessage.setFillParent(true);
        tableMessage.top();

        labelMessage = new Label("", skin);
        labelMessage.setColor(Color.WHITE);
        labelMessage.setAlignment(Align.center);
        tableMessage.add(labelMessage).expandX().padTop(Constant.CONSTANT_TABLE_MESSAGE_PAD_TOP);
        stage.addActor(tableMessage);
    }

    private void clickListenerHandler() {
        loginField.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                loginField.setText("");
            }
        });

        passwordField.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                passwordField.setText("");
                passwordField.setPasswordMode(true);
                passwordField.setPasswordCharacter('*');
            }
        });

        registerButton.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                Assets.clickButton.play();
                stage.addAction(Actions.sequence(Actions.fadeOut(1), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        zombieShooterGame.setScreen(new RegisterScreen(zombieShooterGame));
                    }
                })));
            }
        });

        loginButton.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                Assets.clickButton.play();
                if (!offlineModeSelect) {
                    if ((loginField.getText().toString().matches(Constant.USER_PATTERN))
                            && (passwordField.getText().toString().matches(Constant.PASSWORD_PATTERN))) {
                        login(loginField.getText(), passwordField.getText(), true);
                    } else {
                        labelMessage.setText("Invalid username or password.");
                    }
                } else {
                    //This set resources for the game.
                    User.getSingletonUser().setWeapon(1);
                    User.getSingletonUser().setLevel(6);
                    User.getSingletonUser().setScore(0);
                    User.getSingletonUser().setWeaponOneUnlock(1);
                    User.getSingletonUser().setWeaponTwoUnlock(2);
                    User.getSingletonUser().setWeaponTreeUnlock(3);
                    stage.addAction(Actions.sequence(Actions.fadeOut(1), Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            zombieShooterGame.setScreen(new PlayScreen(zombieShooterGame));
                        }
                    })));
                }
            }
        });

        offlineModeImage.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                Assets.clickButton.play();
                if (currentColor.equals("red")) {
                    currentColor = "green";
                    offlineModeSprite = new Sprite(Assets.offlineModeGreenImage);
                    offlineModeSprite.setColor(0, 1, 0, 0.7f);
                    offlineModeSpriteDrawable = new SpriteDrawable(offlineModeSprite);
                    offlineModeImage = new Image(offlineModeSpriteDrawable);
                    clickListenerHandler();
                    initializationContainer();
                    offlineModeSelect = true;
                } else {
                    currentColor = "red";
                    offlineModeSprite = new Sprite(Assets.offlineModeRedImage);
                    Assets.spriteDefaultColor(offlineModeSprite);
                    offlineModeSpriteDrawable = new SpriteDrawable(offlineModeSprite);
                    offlineModeImage = new Image(offlineModeSpriteDrawable);
                    clickListenerHandler();
                    initializationContainer();
                    offlineModeSelect = false;
                }
            }
        });
    }

    protected static void stopMenuMusic() {
        Assets.gameMenuMusic.stop();
    }

    private static void gameMenuMusic() {
        Assets.gameMenuMusic.play();
        Assets.gameMenuMusic.setLooping(true);
    }

    protected void login(String user, String password, final boolean loginScreenStart) {
        JsonObject json = new JsonObject();
        json.add("username", new JsonPrimitive(user));
        json.add("password", new JsonPrimitive(password));

        final Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
        httpRequest.setUrl(Assets.HTTP_SERVER + "login");
        httpRequest.setHeader("Content-Type", "application/json");
        httpRequest.setContent(json.toString());
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(final Net.HttpResponse httpResponse) {
                if (httpResponse.getStatus().getStatusCode() == 200) {
                    Gson gson = new Gson();
                    JsonElement element = gson.fromJson(httpResponse.getResultAsString(), JsonElement.class);
                    JsonObject jsonObj = element.getAsJsonObject();
                    User.getSingletonUser().setUserId(jsonObj.get("userId").getAsInt()); // Initialization of User Id via POST request
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            if (loginScreenStart) {
                                stage.addAction(Actions.sequence(Actions.fadeOut(1), Actions.run(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadUserInformation();
                                        weaponsStoreJson();
                                        zombieShooterGame.setScreen(new PlayScreen(zombieShooterGame));
                                    }
                                })));
                            } else {
                                loadUserInformation();
                                weaponsStoreJson();
                            }
                        }
                    });
                } else {
                    labelMessage.setText("Check username and password or register.");
                }
            }

            @Override
            public void failed(Throwable t) {
                labelMessage.setText("Please check your Internet connection.");
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

    private void loadUserInformation() {
        final Net.HttpRequest httpGet = new Net.HttpRequest(Net.HttpMethods.GET);
        httpGet.setUrl(Assets.HTTP_SERVER + "userInfoManager?userId=" + User.getSingletonUser().getUserId());
        Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Gson gson = new Gson();
                JsonElement element = gson.fromJson(httpResponse.getResultAsString(), JsonElement.class);
                JsonObject jsonObj = element.getAsJsonObject();
                User.getSingletonUser().setWeapon(jsonObj.get("weapon").getAsJsonObject().get("type").getAsInt());
                User.getSingletonUser().setLevel(jsonObj.get("level").getAsInt());
                User.getSingletonUser().setScore(jsonObj.get("score").getAsInt());
            }

            @Override
            public void failed(Throwable t) {
                labelMessage.setText("Please check your Internet connection.");
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

    private void weaponsStoreJson() {
        final Net.HttpRequest httpGet = new Net.HttpRequest(Net.HttpMethods.GET);
        httpGet.setUrl(Assets.HTTP_SERVER + "weaponsStore?userId=" + User.getSingletonUser().getUserId());
        Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Gson gson = new Gson();
                JsonElement element = gson.fromJson(httpResponse.getResultAsString(), JsonElement.class);
                JsonObject jsonObj = element.getAsJsonObject();
                User.getSingletonUser().setWeaponOneUnlock(jsonObj.get("unlockedWeapons").getAsJsonArray().get(0).getAsJsonObject().get("type").getAsInt());
                User.getSingletonUser().setWeaponTwoUnlock(jsonObj.get("unlockedWeapons").getAsJsonArray().get(1).getAsJsonObject().get("type").getAsInt());
                User.getSingletonUser().setWeaponTreeUnlock(jsonObj.get("unlockedWeapons").getAsJsonArray().get(2).getAsJsonObject().get("type").getAsInt());
            }

            @Override
            public void failed(Throwable t) {
                if (t instanceof IndexOutOfBoundsException) {
                    System.out.print("This is ok!");
                } else {
                    labelMessage.setText("Please check your Internet connection.");
                }
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {

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
        skin.dispose();
        stage.dispose();
        batch.dispose();
        zombieShooterGame.dispose();
    }

}