/*
 * Copyright (C) 2014  Andrew Gunnerson <andrewgunnerson@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.chenxiaolong.multibootpatcher.patcher;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.chenxiaolong.dualbootpatcher.R;

public class ProgressCW implements PatcherUIListener {
    private static final String EXTRA_PROGRESS_MAX = "progress_max";
    private static final String EXTRA_PROGRESS_CURRENT = "progress_current";

    private CardView vCard;
    private TextView vPercentage;
    private TextView vFiles;
    private ProgressBar vProgress;

    private Context mContext;
    private PatcherConfigState mPCS;

    private int mCurProgress = 0;
    private int mMaxProgress = 0;

    public ProgressCW(Context context, PatcherConfigState pcs, CardView card) {
        mContext = context;
        mPCS = pcs;

        vCard = card;
        vPercentage = (TextView) card.findViewById(R.id.progress_percentage);
        vFiles = (TextView) card.findViewById(R.id.progress_files);
        vProgress = (ProgressBar) card.findViewById(R.id.progress_bar);
    }

    private void updateProgress() {
        if (vProgress != null) {
            vProgress.setMax(mMaxProgress);
            vProgress.setProgress(mCurProgress);
        }
        if (vPercentage != null) {
            if (mMaxProgress == 0) {
                vPercentage.setText("0%");
            } else {
                vPercentage.setText(String.format("%.1f%%",
                        (double) mCurProgress / mMaxProgress * 100));
            }
        }
        if (vFiles != null) {
            vFiles.setText(String.format(mContext.getString(R.string.overall_progress_files),
                    Integer.toString(mCurProgress), Integer.toString(mMaxProgress)));
        }
    }

    public void setProgress(int i) {
        mCurProgress = i;
        updateProgress();
    }

    public void setMaxProgress(int i) {
        mMaxProgress = i;
        updateProgress();
    }

    @Override
    public void onCardCreate() {
        switch (mPCS.mState) {
        case PatcherConfigState.STATE_PATCHING:
            vCard.setVisibility(View.VISIBLE);
            break;

        case PatcherConfigState.STATE_CHOSE_FILE:
        case PatcherConfigState.STATE_INITIAL:
        case PatcherConfigState.STATE_FINISHED:
            vCard.setVisibility(View.GONE);
            break;
        }

        updateProgress();
    }

    @Override
    public void onRestoreCardState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mMaxProgress = savedInstanceState.getInt(EXTRA_PROGRESS_MAX);
            mCurProgress = savedInstanceState.getInt(EXTRA_PROGRESS_CURRENT);
            updateProgress();
        }
    }

    @Override
    public void onSaveCardState(Bundle outState) {
        outState.putInt(EXTRA_PROGRESS_MAX, mMaxProgress);
        outState.putInt(EXTRA_PROGRESS_CURRENT, mCurProgress);
    }

    @Override
    public void onChoseFile() {
    }

    @Override
    public void onStartedPatching() {
        vCard.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFinishedPatching() {
        vCard.setVisibility(View.GONE);

        mCurProgress = 0;
        mMaxProgress = 0;
        updateProgress();
    }
}
