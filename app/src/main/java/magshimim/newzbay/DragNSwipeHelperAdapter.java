package magshimim.newzbay;

public interface DragNSwipeHelperAdapter
{
    void onItemMove(int fromPosition, int toPosition); //When item moved

    void onItemDismiss(int position); //When item swiped
}
