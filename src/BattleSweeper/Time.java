package BattleSweeper;
//시간 함수
public class Time {
    private int second;


    public Time(int second) {

        this.second = second;
    }

    public Time(String currentTime) 
    {
        String[] time = currentTime.split("");
        second = Integer.parseInt(time[0]);
        
    }

    public String getCurrentTime()
    {
        return second + " " ;
    }

    public void oneSecondPassed(){
        second++;
    }
}

