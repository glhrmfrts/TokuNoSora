package com.habboi.tns.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.habboi.tns.ui.Menu;

/**
 * Map controller/keyboard input to virtual buttons
 */
public class InputManager extends ControllerAdapter implements InputProcessor {
    // buttons
    public static final int Select = 0;
    public static final int Jump = 1;
    public static final int Up = 2;
    public static final int Down = 3;
    public static final int Left = 4;
    public static final int Right = 5;
    public static final int Back = 6;
    public static final int Pause = 7;
    public static final int Action1 = 8;
    public static final int Action2 = 9;

    // axes
    public static final int Horizontal = 0;
    public static final int Vertical = 1;
    public static final int Acceleration = 2;
    public static final int LookHorizontal = 3;
    public static final int LookVertical = 4;

    public static final float DEAD_ZONE = 0.2f;
    public static final int NUM_BUTTONS = 10;
    public static final int NUM_AXIS = 5;

    public static final String TAG = InputManager.class.getName();

    private static InputManager instance;

    public int screenX, screenY;
    private int[] buttons = new int[NUM_BUTTONS];
    private int[] prevButtons = new int[NUM_BUTTONS];
    private float[] axis = new float[NUM_AXIS];
    private boolean[] axisMenu = new boolean[NUM_AXIS];
    private Controller currentController;
    private int touchingDown;
    private InputMultiplexer multiplexer = new InputMultiplexer();

    public static InputManager getInstance() {
        if (instance == null) {
            instance = new InputManager();
        }
        return instance;
    }

    public static float axisDeadZoneValue(float value) {
        if (Math.abs(value) > DEAD_ZONE) {
            return value;
        }

        return 0;
    }

    private InputManager() {
        multiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(multiplexer);
        Controllers.addListener(this);
    }

    public void addInputProcessor(InputProcessor processor) {
        multiplexer.addProcessor(processor);
    }

    public void removeInputProcessor(InputProcessor processor) {
        multiplexer.removeProcessor(processor);
    }

    public boolean isButtonDown(int button) {
        return buttons[button] > 0;
    }

    public boolean isButtonJustDown(int button) {
        return isButtonDown(button) && !(prevButtons[button] > 0);
    }

    public boolean isAnyButtonDown() {
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isTouchingDown() {
        return touchingDown > 0;
    }

    public float getAxis(int axisIndex) {
        return axis[axisIndex];
    }

    public void reset() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = 0;
            prevButtons[i] = 0;
        }

        for (int i = 0; i < axis.length; i++) {
            axis[i] = 0;
        }
    }

    public void update() {
        System.arraycopy(buttons, 0, prevButtons, 0, buttons.length);
    }

    public void menuInteraction(Menu menu) {
        if (axis[Horizontal] == 0) {
            axisMenu[Horizontal] = false;
        }
        if (axis[Vertical] == 0) {
            axisMenu[Vertical] = false;
        }

        if (!axisMenu[Horizontal]) {
            if (axis[Horizontal] >= 0.8f) {
                menu.buttonDown(Right);
                axisMenu[Horizontal] = true;
            } else if (axis[Horizontal] <= -0.8f) {
                menu.buttonDown(Left);
                axisMenu[Horizontal] = true;
            }
        }

        if (!axisMenu[Vertical]) {
            if (axis[Vertical] >= 0.8f) {
                menu.buttonDown(Up);
                axisMenu[Vertical] = true;
            } else if (axis[Vertical] <= -0.8f) {
                menu.buttonDown(Down);
                axisMenu[Vertical] = true;
            }       
        }

        if (isButtonJustDown(Select)) {
            menu.buttonDown(Select);
        } else if (isButtonJustDown(Back)) {
            menu.buttonDown(Back);
        }
    }

    @Override
    public void connected(Controller controller) {
        if (currentController == null) {
            currentController = controller;
        }
    }

    @Override
    public void disconnected(Controller controller) {
        if (currentController == controller) {
            currentController = null;
        }
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        value = axisDeadZoneValue(value);
        if (value != 0)
            Gdx.app.log(TAG, "axis: " + axisCode + " = " + value);

        if (axisCode == Xbox.L_STICK_HORIZONTAL_AXIS) {
            axis[Horizontal] = value;
        }
        if (axisCode == Xbox.L_STICK_VERTICAL_AXIS) {
            axis[Vertical] = -value;
        }
        if (axisCode == Xbox.R_STICK_HORIZONTAL_AXIS) {
            axis[LookHorizontal] = value;
        }
        if (axisCode == Xbox.R_STICK_VERTICAL_AXIS) {
            axis[LookVertical] = -value;
        }
        if (axisCode == Xbox.R_TRIGGER || axisCode == Xbox.L_TRIGGER) {
            axis[Acceleration] = -value;
        }

        return true;
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        Gdx.app.log(TAG, "button: " + buttonCode);

        if (buttonCode == Xbox.A) {
            buttons[Jump]++;
            buttons[Select]++;
            return true;
        }
        if (buttonCode == Xbox.B) {
            buttons[Back]++;
            return true;
        }
        if (buttonCode == Xbox.START) {
            buttons[Pause]++;
            return true;
        }
        if (buttonCode == Xbox.L_BUMPER) {
            buttons[Action1]++;
            return true;
        }
        if (buttonCode == Xbox.R_BUMPER) {
            buttons[Action2]++;
            return true;
        }

        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        if (buttonCode == Xbox.A) {
            buttons[Jump] = 0;
            buttons[Select] = 0;
            return true;
        }
        if (buttonCode == Xbox.B) {
            buttons[Back] = 0;
            return true;
        }
        if (buttonCode == Xbox.START) {
            buttons[Pause] = 0;
            return true;
        }
        if (buttonCode == Xbox.L_BUMPER) {
            buttons[Action1] = 0;
            return true;
        }
        if (buttonCode == Xbox.R_BUMPER) {
            buttons[Action2] = 0;
            return true;
        }
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
        case Input.Keys.LEFT:
            buttons[Left]++;
            axis[Horizontal] = -1;
            return true;

        case Input.Keys.RIGHT:
            buttons[Right]++;
            axis[Horizontal] = 1;
            return true;

        case Input.Keys.UP:
            buttons[Up]++;
            axis[Vertical] = 1;
            return true;

        case Input.Keys.DOWN:
            buttons[Down]++;
            axis[Vertical] = -1;
            return true;

        case Input.Keys.SPACE:
            buttons[Jump]++;
            return true;

        case Input.Keys.ENTER:
            buttons[Select]++;
            return true;

        case Input.Keys.ESCAPE:
            buttons[Back]++;
            buttons[Pause]++;
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
        case Input.Keys.LEFT:
            buttons[Left] = 0;
            axis[Horizontal] = 0;
            return true;

        case Input.Keys.RIGHT:
            buttons[Right] = 0;
            axis[Horizontal] = 0;
            return true;

        case Input.Keys.UP:
            buttons[Up] = 0;
            axis[Vertical] = 0;
            return true;

        case Input.Keys.DOWN:
            buttons[Down] = 0;
            axis[Vertical] = 0;
            return true;

        case Input.Keys.SPACE:
            buttons[Jump] = 0;
            return true;

        case Input.Keys.ENTER:
            buttons[Select] = 0;
            return true;

        case Input.Keys.ESCAPE:
            buttons[Back] = 0;
            buttons[Pause] = 0;
            return true;
        }
        return false;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        Gdx.app.log(TAG, "pov: " + povCode + " = " + value);

        if (povCode == 0) {
            if (value == PovDirection.center) {
                buttons[Left] = 0;
                buttons[Right] = 0;
                buttons[Up] = 0;
                buttons[Down] = 0;
                axis[Horizontal] = 0;
                axis[Vertical] = 0;
            }

            if (value == PovDirection.east) {
                buttons[Right]++;
                axis[Horizontal] = 1;
            }
            if (value == PovDirection.west) {
                buttons[Left]++;
                axis[Horizontal] = -1;
            }
            if (value == PovDirection.north) {
                buttons[Up]++;
                axis[Vertical] = 1;
            }
            if (value == PovDirection.south) {
                buttons[Down]++;
                axis[Vertical] = -1;
            }
            if (value == PovDirection.northEast) {
                buttons[Right]++;
                axis[Horizontal] = 1;
                buttons[Up]++;
                axis[Vertical] = 1;
            }
            if (value == PovDirection.northWest) {
                buttons[Left]++;
                axis[Horizontal] = -1;
                buttons[Up]++;
                axis[Vertical] = 1;
            }
            if (value == PovDirection.southEast) {
                buttons[Right]++;
                axis[Horizontal] = 1;
                buttons[Down]++;
                axis[Vertical] = -1;
            }
            if (value == PovDirection.southWest) {
                buttons[Left]++;
                axis[Horizontal] = -1;
                buttons[Down]++;
                axis[Vertical] = -1;
            }
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
      touchingDown++;
      return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
      touchingDown--;
      return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
      this.screenX = screenX;
      this.screenY = screenY;
      return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
      this.screenX = screenX;
      this.screenY = screenY;
      return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    /**
     * This class wasn't on libGDX's controllers.mappings package
     * for some reason, so I copied and pasted from github into here.
     * I only added the windows mappings, original author is @badlogic.
     */
    public static class Xbox {
        // Buttons
        public static final int A;
        public static final int B;
        public static final int X;
        public static final int Y;
        public static final int GUIDE;
        public static final int L_BUMPER;
        public static final int R_BUMPER;
        public static final int BACK;
        public static final int START;
        public static final int DPAD_UP;
        public static final int DPAD_DOWN;
        public static final int DPAD_LEFT;
        public static final int DPAD_RIGHT;

        // Axes
        /** left trigger, -1 if not pressed, 1 if pressed **/
        public static final int L_TRIGGER;
        /** right trigger, -1 if not pressed, 1 if pressed **/
        public static final int R_TRIGGER;
        /** left stick vertical axis, -1 if up, 1 if down **/
        public static final int L_STICK_VERTICAL_AXIS;
        /** left stick horizontal axis, -1 if left, 1 if right **/
        public static final int L_STICK_HORIZONTAL_AXIS;
        /** right stick vertical axis, -1 if up, 1 if down **/
        public static final int R_STICK_VERTICAL_AXIS;
        /** right stick horizontal axis, -1 if left, 1 if right **/
        public static final int R_STICK_HORIZONTAL_AXIS;

        static {
            if (SharedLibraryLoader.isWindows) {
                A = 0;
                B = 1;
                X = 2;
                Y = 3;
                GUIDE = -1;
                L_BUMPER = 4;
                R_BUMPER = 5;
                BACK = 6;
                START = 7;
                DPAD_UP = -1;
                DPAD_DOWN = -1;
                DPAD_LEFT = -1;
                DPAD_RIGHT = -1;
                L_TRIGGER = 4;
                R_TRIGGER = 4;
                L_STICK_VERTICAL_AXIS = 0;
                L_STICK_HORIZONTAL_AXIS = 1;
                R_STICK_VERTICAL_AXIS = 2;
                R_STICK_HORIZONTAL_AXIS = 3;
            } else if (SharedLibraryLoader.isLinux) {
                // TODO: set linux mappings
                A = 0;
                B = 1;
                X = 2;
                Y = 3;
                GUIDE = -1;
                L_BUMPER = -1;
                R_BUMPER = -1;
                BACK = -1;
                START = -1;
                DPAD_UP = -1;
                DPAD_DOWN = -1;
                DPAD_LEFT = -1;
                DPAD_RIGHT = -1;
                L_TRIGGER = -1;
                R_TRIGGER = -1;
                L_STICK_VERTICAL_AXIS = -1;
                L_STICK_HORIZONTAL_AXIS = -1;
                R_STICK_VERTICAL_AXIS = -1;
                R_STICK_HORIZONTAL_AXIS = -1;
            } else if (SharedLibraryLoader.isMac) {
                A = 11;
                B = 12;
                X = 13;
                Y = 14;
                GUIDE = 10;
                L_BUMPER = 8;
                R_BUMPER = 9;
                BACK = 5;
                START = 4;
                DPAD_UP = 0;
                DPAD_DOWN = 1;
                DPAD_LEFT = 2;
                DPAD_RIGHT = 3;
                L_TRIGGER = 0;
                R_TRIGGER = 1;
                L_STICK_VERTICAL_AXIS = 3;
                L_STICK_HORIZONTAL_AXIS = 2;
                R_STICK_VERTICAL_AXIS = 5;
                R_STICK_HORIZONTAL_AXIS = 4;
            } else {
                A = -1;
                B = -1;
                X = -1;
                Y = -1;
                GUIDE = -1;
                L_BUMPER = -1;
                R_BUMPER = -1;
                L_TRIGGER = -1;
                R_TRIGGER = -1;
                BACK = -1;
                START = -1;
                DPAD_UP = -1;
                DPAD_DOWN = -1;
                DPAD_LEFT = -1;
                DPAD_RIGHT = -1;
                L_STICK_VERTICAL_AXIS = -1;
                L_STICK_HORIZONTAL_AXIS = -1;
                R_STICK_VERTICAL_AXIS = -1;
                R_STICK_HORIZONTAL_AXIS = -1;
            }
        }
        
        /** @return whether the {@link Controller} is an Xbox controller
         */
        public static boolean isXboxController(Controller controller) {
            return controller.getName().contains("Xbox");
        }
    }
}