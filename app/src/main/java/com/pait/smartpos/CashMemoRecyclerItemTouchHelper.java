package com.pait.smartpos;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.pait.smartpos.adpaters.AddToCartRecyclerAdapter;
import com.pait.smartpos.constant.Constant;

public class CashMemoRecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private CashMemoRecyclerItemTouchHelper.RecyclerItemTouchHelperListener listener;

    public CashMemoRecyclerItemTouchHelper(int dragDirs, int swipeDirs, CashMemoRecyclerItemTouchHelper.RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        //Constant.showLog("onChildDraw");
        final View foregroundView = ((AddToCartRecyclerAdapter.AddToCartViewHolder) viewHolder).viewForeground;
        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,actionState, isCurrentlyActive);

        final View delbgView = ((AddToCartRecyclerAdapter.AddToCartViewHolder) viewHolder).viewBackground;
        if (dX>0){
            delbgView.setVisibility(View.VISIBLE);
        } else {
            delbgView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //Constant.showLog("onSwiped");
        if (direction == ItemTouchHelper.LEFT){
            listener.startNewActivty(viewHolder, direction, viewHolder.getAdapterPosition());
        } else {
            listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            final View foregroundView = ((AddToCartRecyclerAdapter.AddToCartViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        //Constant.showLog("onChildDrawOver");
        final View foregroundView = ((AddToCartRecyclerAdapter.AddToCartViewHolder) viewHolder).viewForeground;
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //Constant.showLog("clearView");
        final View foregroundView = ((AddToCartRecyclerAdapter.AddToCartViewHolder) viewHolder).viewForeground;
        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        //Constant.showLog("convertToAbsoluteDirection");
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }


    public interface RecyclerItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
        void startNewActivty(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }

    /*public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        if (actionState == ACTION_STATE_SWIPE) {
            setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void setTouchListener(final Canvas c, final RecyclerView recyclerView,
                                  final RecyclerView.ViewHolder viewHolder, final float dX, final float dY,
                                  final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                if (swipeBack) {
                    Constant.showLog("dx "+dX);
                    if (dX < -buttonWidth) {
                        buttonShowedState = ButtonsState.RIGHT_VISIBLE;
                    }else if (dX > buttonWidth) {
                        buttonShowedState  = ButtonsState.LEFT_VISIBLE;
                    }
                    if (buttonShowedState != ButtonsState.GONE) {
                        setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        setItemsClickable(recyclerView, false);
                    }
                }
                return false;
            }
        });
    }

    private void setTouchDownListener(final Canvas c,
                                      final RecyclerView recyclerView,
                                      final RecyclerView.ViewHolder viewHolder,
                                      final float dX, final float dY,
                                      final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                return false;
            }
        });
    }

    private void setTouchUpListener(final Canvas c,
                                    final RecyclerView recyclerView,
                                    final RecyclerView.ViewHolder viewHolder,
                                    final float dX, final float dY,
                                    final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    RecyclerItemTouchHelper.super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                    recyclerView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                    setItemsClickable(recyclerView, true);
                    swipeBack = false;
                    buttonShowedState = ButtonsState.GONE;
                }
                return false;
            }
        });
    }

    private void setItemsClickable(RecyclerView recyclerView,
                                   boolean isClickable) {
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack) {
            swipeBack = false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }*/
}
