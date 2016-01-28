//随手势上滑隐藏，下滑显示
@Override
public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    if (create_btn != null && create_btn.getVisibility() == View.VISIBLE) {
        if (distanceY > EaseMobConfig.SCROLL_OFFSET && isVisibility) {
            isVisibility = false;
            Animator animator = ObjectAnimator.ofFloat(create_btn, EaseMobConfig.SCROLL_TRANSLATIONY, 0, create_btn.getMeasuredHeight());
            animator.start();
        } else if (distanceY < EaseMobConfig.SCROLL_OFFSET && !isVisibility) {
            isVisibility = true;
            Animator animator = ObjectAnimator.ofFloat(create_btn, EaseMobConfig.SCROLL_TRANSLATIONY, create_btn.getTranslationY(), 0);
            animator.start();
        }
    }
    return true;
}


	    //section头，比如分组ABCD，这种section悬浮在顶部
	    @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > count) {
                    showSection((YGInsuranceItem) view.getItemAtPosition(firstVisibleItem), firstVisibleItem);
                    int index = -1;
                    for (int i = 0; i < visibleItemCount; i++) {
                        if (view.getChildAt(i).getId() == firstVisibleItem) {
                            index = i;
                            break;
                        }
                    }
                    View nextView = view.getChildAt(index);
                    if (nextView != null) {
                        int b1 = nextView.getTop();
                        int b2 = section_card_rl.getMeasuredHeight() + section_card_rl.getPaddingTop();
                        if (b1 <= b2 && b1 > 0) {
                            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, b1 - b2, 0, 0);
                            section_card_rl.setLayoutParams(params);
                        } else {
                            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, 0, 0, 0);
                            section_card_rl.setLayoutParams(params);
                        }
                    } else {
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 0, 0, 0);
                        section_card_rl.setLayoutParams(params);
                    }
                } else {
                    hideSection();
                }
            }
