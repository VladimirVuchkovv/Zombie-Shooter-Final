package bg.ittalents.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import bg.ittalents.game.Resource.Assets;
import bg.ittalents.game.Resource.Constant;


public class RegisterScreen extends LoginScreen implements Screen {
    private static final float CONSTANT_PAD_BOTTOM = Constant.HEIGHT_SCREEN / 12;
    private static final String RE_PASSWORD = "RE-PASSWORD";
    private static final String EMAIL = "EMAIL";

    private Game game;
    private Skin skin;
    private Stage stage;
    private SpriteBatch batch;
    private Sprite backGroundSprite;
    private Table container;
    private Table containerFirstRow;
    private Table containerSecondRow;
    private TextField userField;
    private TextField passwordField;
    private TextField passwordCheckField;
    private TextField emailField;
    private ImageButton registerButton;
    private Image imageTitle;
    private Label labelMessage;
    private Table tableMessage;

    protected RegisterScreen(Game game) {
        super(game);
        this.game = game;
    }

    @Override
    public void show() {

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();

        Gdx.input.setInputProcessor(stage);

        // Adding the game title to the screen
        Sprite spriteTitle = new Sprite(Assets.zombieShooterTitle);
        Assets.spriteDefaultColorSolid(spriteTitle);
        SpriteDrawable spriteDrawableTitle = new SpriteDrawable(spriteTitle);
        imageTitle = new Image(spriteDrawableTitle);

        // Creating all the containers for the positioning and arranging of our stuff
        container = new Table();
        container.setWidth(stage.getWidth());
        container.align(Align.center | Align.top);
        container.setPosition(0, Constant.HEIGHT_SCREEN);

        containerFirstRow = new Table();
        containerFirstRow.setWidth(stage.getWidth());
        containerFirstRow.align(Align.center | Align.top);
        containerFirstRow.setPosition(0, Constant.HEIGHT_SCREEN);

        containerSecondRow = new Table();
        containerSecondRow.setWidth(stage.getWidth());
        containerSecondRow.align(Align.center | Align.top);
        containerSecondRow.setPosition(0, Constant.HEIGHT_SCREEN);

        creatingRegisterButton();

        creatingTheTextFields();

        // Setting the text field text to be on the center
        settingTextFieldsTextToCenter();

        // Remove any text in hte text fields on click so that user can write his information
        cleaningTextFieldsToBlank();


        //Adding all the object into the stage. First of all we add to the container
        // for the row then to the main container , finally into the main stage
        arrangingTheScreen();

        // Creating the background
        backGroundSprite = new Sprite(Assets.backgroundMenu);
        backGroundSprite.setSize(Constant.WIDTH_SCREEN, Constant.HEIGHT_SCREEN);

        // Setting the back key on android to true, so it can accept interaction
        Gdx.input.setCatchBackKey(true);

        inizializiraneWarningMessage();

        // When clicking on the register button we start the validation process.
        // It calls all the methods which are checking the user input
        registerButton.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                Assets.clickButton.play();
                if ((checkUserField(userField.getText().toString())) && (checkPasswordField(passwordField.getText().toString()))
                        && (checkPasswordAndRePassword(passwordField.getText().toString(), passwordCheckField.getText().toString()))
                        && (checkEmailField(emailField.getText().toString()))) {
                    registerJson();
                }
            }
        });
    }

    private void inizializiraneWarningMessage() {
        tableMessage = new Table();
        tableMessage.setFillParent(true);
        tableMessage.top();

        labelMessage = new Label("", skin);
        labelMessage.setColor(Color.WHITE);
        labelMessage.setAlignment(Align.center);
        tableMessage.add(labelMessage).expandX().padTop(Constant.CONSTANT_TABLE_MESSAGE_PAD_TOP);
        stage.addActor(tableMessage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        backGroundSprite.draw(batch);
        batch.draw(backGroundSprite, backGroundSprite.getX(), backGroundSprite.getY(), backGroundSprite.getWidth(), backGroundSprite.getHeight());
        batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            game.setScreen(new LoginScreen(game));
        }
    }

    private void creatingRegisterButton() {
        Sprite spriteRegisterButton = new Sprite(
                Assets.registerButton);
        Assets.spriteDefaultColor(spriteRegisterButton);
        spriteRegisterButton.setSize((Constant.CONSTANT_WIDTH_FIELD_AND_BUTTON), (Constant.CONSTANCE_HEIGHT_BUTTONS));
        SpriteDrawable registerSpriteDrawable = new SpriteDrawable(spriteRegisterButton);
        registerButton = new ImageButton(registerSpriteDrawable);
    }

    private void creatingTheTextFields() {
        userField = new TextField(Constant.USER_NAME, skin);
        userField.setColor(1, 0, 0, 0.5f);
        passwordField = new TextField(Constant.PASSWORD, skin);
        passwordField.setColor(1, 0, 0, 0.5f);
        passwordCheckField = new TextField(RE_PASSWORD, skin);
        passwordCheckField.setColor(1, 0, 0, 0.5f);
        emailField = new TextField(EMAIL, skin);
        emailField.setColor(1, 0, 0, 0.5f);
    }

    private void settingTextFieldsTextToCenter() {
        userField.setAlignment(Align.center);
        passwordField.setAlignment(Align.center);
        passwordCheckField.setAlignment(Align.center);
        emailField.setAlignment(Align.center);
    }

    private void cleaningTextFieldsToBlank() {
        userField.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                userField.setText("");
            }
        });

        passwordField.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                passwordField.setText("");
                passwordField.setPasswordMode(true);
                passwordField.setPasswordCharacter('*');
            }
        });

        passwordCheckField.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                passwordCheckField.setText("");
                passwordCheckField.setPasswordMode(true);
                passwordCheckField.setPasswordCharacter('*');
            }
        });

        emailField.addListener(new ClickListener() {
            public void clicked(InputEvent e, float x, float y) {
                emailField.setText("");
            }
        });
    }

    private void registerJson() {
        JsonObject json = new JsonObject();
        json.add("username", new JsonPrimitive(userField.getText()));
        json.add("password", new JsonPrimitive(passwordField.getText()));
        json.add("email", new JsonPrimitive(emailField.getText()));

        final Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
        httpRequest.setUrl(Assets.HTTP_SERVER + "registration");
        httpRequest.setHeader("Content-Type", "application/json");
        httpRequest.setContent(json.toString());
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(final Net.HttpResponse httpResponse) {
                System.out.print(httpResponse.getResultAsString());
                if (httpResponse.getStatus().getStatusCode() == 200) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            stage.addAction(Actions.sequence(Actions.fadeOut(1), Actions.run(new Runnable() {
                                @Override
                                public void run() {
                                    login(userField.getText().toString(), passwordField.getText().toString(), false);
                                    game.setScreen(new PlayScreen(game));
                                }
                            })));
                        }
                    });
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

    private void arrangingTheScreen() {
        container.add(imageTitle).width(Constant.WIDTH_SCREEN)
                .height((Constant.CONSTANT_HEIGHT_TITLE)).padBottom(CONSTANT_PAD_BOTTOM);
        container.row();
        containerFirstRow.add(userField).width(Constant.CONSTANT_WIDTH_FIELD_AND_BUTTON)
                .height(Constant.CONSTANT_HEIGHT_FIELD_AND_BUTTON).padBottom(CONSTANT_PAD_BOTTOM)
                .padRight(Constant.CONSTANT_PAD_LEFT_AND_RIGHT);
        containerFirstRow.add(passwordField).width(Constant.CONSTANT_WIDTH_FIELD_AND_BUTTON)
                .height(Constant.CONSTANT_HEIGHT_FIELD_AND_BUTTON).padBottom(CONSTANT_PAD_BOTTOM)
                .padLeft(Constant.CONSTANT_PAD_LEFT_AND_RIGHT);
        container.add(containerFirstRow);
        container.row();
        containerSecondRow.add(emailField).width(Constant.CONSTANT_WIDTH_FIELD_AND_BUTTON)
                .height(Constant.CONSTANT_HEIGHT_FIELD_AND_BUTTON).padBottom(CONSTANT_PAD_BOTTOM)
                .padRight(Constant.CONSTANT_PAD_LEFT_AND_RIGHT);
        containerSecondRow.add(passwordCheckField).width(Constant.CONSTANT_WIDTH_FIELD_AND_BUTTON)
                .height(Constant.CONSTANT_HEIGHT_FIELD_AND_BUTTON).padBottom(CONSTANT_PAD_BOTTOM)
                .padLeft(Constant.CONSTANT_PAD_LEFT_AND_RIGHT);
        container.add(containerSecondRow);
        container.row();
        container.add(registerButton);

        stage.addActor(container);
    }

    private boolean checkUserField(String logField) {
        if (logField.matches(Constant.USER_PATTERN)) {
            return true;
        }
        labelMessage.setText("Username must contain at least one letter and be 3-10 characters.");
        return false;
    }

    private boolean checkPasswordField(String passwordText) {
        if (passwordText.matches(Constant.PASSWORD_PATTERN)) {
            return true;
        } else {
            labelMessage.setText("Password must contain a capital letter, \n one lowercase letter, one number and be 5-10 characters");
            return false;
        }
    }

    private boolean checkPasswordAndRePassword(String password, String rePassword) {
        if (password.equals(rePassword)) {
            return true;
        } else {
            labelMessage.setText("Password and Re-Password must be the same");
            return false;
        }
    }

    private boolean checkEmailField(String emailField) {
        if (emailField.matches(Constant.EMAIL_PATTERN)) {
            return true;
        } else {
            labelMessage.setText("Invalid email");
            return false;
        }
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
        stage.dispose();
        game.dispose();
        skin.dispose();
        batch.dispose();

    }

}