/*
 * This file is part of MAME4droid.
 *
 * Copyright (C) 2013 David Valdeita (Seleuco)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Linking MAME4droid statically or dynamically with other modules is
 * making a combined work based on MAME4droid. Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * In addition, as a special exception, the copyright holders of MAME4droid
 * give you permission to combine MAME4droid with free software programs
 * or libraries that are released under the GNU LGPL and with code included
 * in the standard release of MAME under the MAME License (or modified
 * versions of such code, with unchanged license). You may copy and
 * distribute such a system following the terms of the GNU GPL for MAME4droid
 * and the licenses of the other code concerned, provided that you include
 * the source code of that other code when and as the GNU GPL requires
 * distribution of source code.
 *
 * Note that people who make modified versions of MAME4idroid are not
 * obligated to grant this special exception for their modified versions; it
 * is their choice whether to do so. The GNU General Public License
 * gives permission to release a modified version without this exception;
 * this exception also makes it possible to release a modified version
 * which carries forward this exception.
 *
 * MAME4droid is dual-licensed: Alternatively, you can license MAME4droid
 * under a MAME license, as set out in http://mamedev.org/
 */

package com.yunluo.android.arcadehub.helpers;

import com.yunluo.android.arcadehub.Emulator;
import com.yunluo.android.arcadehub.GamePlayActivity;
import com.yunluo.android.arcadehub.R;
import com.yunluo.android.arcadehub.input.ControlCustomizer;
import com.yunluo.android.arcadehub.popup.OptionPopup;
import com.yunluo.android.arcadehub.utils.Debug;
import com.yunluo.android.arcadehub.utils.FileUtil;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;

public class DialogHelper {

    public static int savedDialog = DialogHelper.DIALOG_NONE;

    public final static int DIALOG_NONE = -1;

    public final static int DIALOG_EXIT = 1;

    public final static int DIALOG_ERROR_WRITING = 2;

    public final static int DIALOG_INFO = 3;

    public final static int DIALOG_EXIT_GAME = 4;

    public final static int DIALOG_OPTIONS = 5;

    public final static int DIALOG_THANKS = 6;

    public final static int DIALOG_FULLSCREEN = 7;

    public final static int DIALOG_LOAD_FILE_EXPLORER = 8;

    public final static int DIALOG_ROMs_DIR = 9;

    public final static int DIALOG_FINISH_CUSTOM_LAYOUT = 10;

    public final static int DIALOG_EMU_RESTART = 11;

    public final static int DIALOG_GAMES_HANGUP = 19;

    private Resources res = null;

    protected GamePlayActivity mm = null;

    protected static String errorMsg;

    protected static String infoMsg;

	private OptionPopup mOptionPopup;

    public void setErrorMsg(String errorMsg) {
        DialogHelper.errorMsg = errorMsg;
    }

    public void setInfoMsg(String infoMsg) {
        DialogHelper.infoMsg = infoMsg;
    }

    public DialogHelper(GamePlayActivity value, OptionPopup popup) {
        mm = value;
        res = mm.getBaseContext().getResources();
        this.mOptionPopup = popup;
    }

    public Dialog createDialog(int id) {

        if (id == DialogHelper.DIALOG_LOAD_FILE_EXPLORER) {
            return mm.getFileExplore().create();
        }
        switch (id) {
            case DIALOG_OPTIONS:
            case DIALOG_FULLSCREEN:
                mOptionPopup.show();
                Emulator.pause();
                return null;
        }

        Dialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(mm);
        switch (id) {
            case DIALOG_FINISH_CUSTOM_LAYOUT:

                builder.setMessage(res.getString(R.string.help_save_message))
                        .setCancelable(false)
                        .setPositiveButton(res.getString(R.string.help_yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        DialogHelper.savedDialog = DIALOG_NONE;
                                        mm.removeDialog(DIALOG_FINISH_CUSTOM_LAYOUT);
                                        ControlCustomizer.setEnabled(false);
                                        mm.getInputHandler().getControlCustomizer()
                                                .saveDefinedControlLayout();
                                        mm.getEmuView().setVisibility(View.VISIBLE);
                                        mm.getEmuView().requestFocus();
                                        Emulator.resume();
                                        mm.getInputView().invalidate();
                                    }
                                })
                        .setNegativeButton(res.getString(R.string.help_no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        DialogHelper.savedDialog = DIALOG_NONE;
                                        mm.removeDialog(DIALOG_FINISH_CUSTOM_LAYOUT);
                                        ControlCustomizer.setEnabled(false);
                                        mm.getInputHandler().getControlCustomizer()
                                                .discardDefinedControlLayout();
                                        mm.getEmuView().setVisibility(View.VISIBLE);
                                        mm.getEmuView().requestFocus();
                                        Emulator.resume();
                                        mm.getInputView().invalidate();
                                    }
                                });
                dialog = builder.create();
                break;
            case DIALOG_ROMs_DIR:

                builder.setMessage(res.getString(R.string.help_rom_path_message))
                        .setCancelable(false)
                        .setPositiveButton(res.getString(R.string.help_yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        DialogHelper.savedDialog = DIALOG_NONE;
                                        mm.removeDialog(DIALOG_ROMs_DIR);
                                        if (mm.getMainHelper().ensureROMsDir(
                                        		FileUtil.getDefaultROMsDIR())) {
                                            mm.getPrefsHelper().setROMsDIR(
                                            		FileUtil.getDefaultROMsDIR());
                                            mm.runMAME4droid();
                                        }
                                    }
                                })
                        .setNegativeButton(res.getString(R.string.help_no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        DialogHelper.savedDialog = DIALOG_NONE;
                                        mm.removeDialog(DIALOG_ROMs_DIR);
                                        mm.showDialog(DialogHelper.DIALOG_LOAD_FILE_EXPLORER);
                                    }
                                });
                dialog = builder.create();
                break;
            case DIALOG_EXIT:

                builder.setMessage(res.getString(R.string.help_exit))
                        .setCancelable(false)
                        .setPositiveButton(res.getString(R.string.help_yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        DialogHelper.savedDialog = DIALOG_NONE;
                                        Emulator.resume();
                                        Emulator.setValue(Emulator.EXIT_GAME_KEY, 1);
                                        try {
                                            Thread.sleep(100);
                                        } catch (InterruptedException e) {
                                        }
                                        Emulator.setValue(Emulator.EXIT_GAME_KEY, 0);
                                        mm.removeDialog(DIALOG_EXIT_GAME);

                                        Emulator.isDisplay = false;
                                        Emulator.resetGame();
                                        //
                                        Emulator.setValue(Emulator.EXIT_GAME, 0);
                                        mm.finish();
                                    }
                                })
                        .setNegativeButton(res.getString(R.string.help_no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Emulator.resume();
                                        DialogHelper.savedDialog = DIALOG_NONE;
                                        mm.removeDialog(DIALOG_EXIT);
                                    }
                                });
                dialog = builder.create();
                break;
            case DIALOG_ERROR_WRITING:
                builder.setMessage(res.getString(R.string.help_writing_error))
                        .setCancelable(false)
                        .setPositiveButton(res.getString(R.string.BTN_COMMON_OK),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        DialogHelper.savedDialog = DIALOG_NONE;
                                        mm.removeDialog(DIALOG_ERROR_WRITING);
                                        mm.showDialog(DialogHelper.DIALOG_LOAD_FILE_EXPLORER);
                                    }
                                });

                dialog = builder.create();
                break;
            case DIALOG_INFO:
                builder.setMessage(res.getString(R.string.help_info))
                        .setCancelable(false)
                        .setPositiveButton(res.getString(R.string.BTN_COMMON_OK),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        DialogHelper.savedDialog = DIALOG_NONE;
                                        Emulator.resume();
                                        mm.removeDialog(DIALOG_INFO);
                                    }
                                });

                dialog = builder.create();
                break;
            case DIALOG_EXIT_GAME:
                builder.setMessage(res.getString(R.string.help_exit_game))
                        .setCancelable(false)
                        .setPositiveButton(res.getString(R.string.help_yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    	Debug.d("DIALOG_EXIT_GAME", "=======");
                                        DialogHelper.savedDialog = DIALOG_NONE;
                                        Emulator.exitGames();
                                        mm.removeDialog(DIALOG_EXIT_GAME);

                                        mm.finish();

                                    }
                                })
                        .setNegativeButton(res.getString(R.string.help_no),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Emulator.resume();
                                        DialogHelper.savedDialog = DIALOG_NONE;
                                        mm.removeDialog(DIALOG_EXIT_GAME);
                                    }
                                });
                dialog = builder.create();
                break;
            case DIALOG_THANKS:
                builder.setMessage(res.getString(R.string.help_thanks))
                        .setCancelable(false)
                        .setPositiveButton(res.getString(R.string.BTN_COMMON_OK),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        DialogHelper.savedDialog = DIALOG_NONE;
                                        mm.getMainHelper().showWeb();
                                        mm.removeDialog(DIALOG_THANKS);
                                    }
                                });

                dialog = builder.create();
                break;
            case DIALOG_EMU_RESTART:
                builder.setTitle(res.getString(R.string.help_restart_title))
                        .setMessage(res.getString(R.string.help_restart_message))
                        .setCancelable(false)
                        .setPositiveButton(res.getString(R.string.help_dismiss),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        PendingIntent intent = PendingIntent.getActivity(
                                                mm.getBaseContext(), 0, new Intent(mm.getIntent()),
                                                mm.getIntent().getFlags());
                                        AlarmManager manager = (AlarmManager)mm
                                                .getSystemService(Context.ALARM_SERVICE);
                                        manager.set(AlarmManager.RTC,
                                                System.currentTimeMillis() + 250, intent);
                                        android.os.Process.killProcess(android.os.Process.myPid());
                                    }
                                });
                dialog = builder.create();
                break;
            case DIALOG_GAMES_HANGUP:
                builder.setCancelable(false);
                builder.setMessage(res.getString(R.string.help_hangup_messgae));
                builder.setPositiveButton(res.getString(R.string.BTN_COMMON_OK),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DialogHelper.savedDialog = DIALOG_NONE;
                                Emulator.gameResumed();
                                Emulator.resume();
                                mm.removeDialog(DIALOG_GAMES_HANGUP);
                            }
                        });

                dialog = builder.create();
                break;
            default:
                dialog = null;
        }
        return dialog;

    }

    public void prepareDialog(int id, Dialog dialog) {

        if (id == DIALOG_ERROR_WRITING) {
            ((AlertDialog)dialog).setMessage(errorMsg);
            DialogHelper.savedDialog = DIALOG_ERROR_WRITING;
        } else if (id == DIALOG_INFO) {
            ((AlertDialog)dialog).setMessage(infoMsg);
            Emulator.pause();
            DialogHelper.savedDialog = DIALOG_INFO;
        } else if (id == DIALOG_THANKS) {
            Emulator.pause();
            DialogHelper.savedDialog = DIALOG_THANKS;
        } else if (id == DIALOG_EXIT) {
            Emulator.pause();
            DialogHelper.savedDialog = DIALOG_EXIT;
        } else if (id == DIALOG_EXIT_GAME) {
            Emulator.pause();
            DialogHelper.savedDialog = DIALOG_EXIT_GAME;
        } else if (id == DIALOG_OPTIONS) {
            Emulator.pause();
            DialogHelper.savedDialog = DIALOG_OPTIONS;
        } else if (id == DIALOG_FULLSCREEN) {
            Emulator.pause();
            DialogHelper.savedDialog = DIALOG_FULLSCREEN;
        } else if (id == DIALOG_ROMs_DIR) {
            DialogHelper.savedDialog = DIALOG_ROMs_DIR;
        } else if (id == DIALOG_LOAD_FILE_EXPLORER) {
            DialogHelper.savedDialog = DIALOG_LOAD_FILE_EXPLORER;
        } else if (id == DIALOG_FINISH_CUSTOM_LAYOUT) {
            DialogHelper.savedDialog = DIALOG_FINISH_CUSTOM_LAYOUT;
        } else if (id == DIALOG_EMU_RESTART) {
            Emulator.pause();
        } else if (id == DIALOG_GAMES_HANGUP) {
            Emulator.pause();
            DialogHelper.savedDialog = DIALOG_GAMES_HANGUP;
        }

    }

    public void removeDialogs() {
        if (savedDialog == DIALOG_FINISH_CUSTOM_LAYOUT) {
            mm.removeDialog(DIALOG_FINISH_CUSTOM_LAYOUT);
            DialogHelper.savedDialog = DIALOG_NONE;
        }
    }

}
