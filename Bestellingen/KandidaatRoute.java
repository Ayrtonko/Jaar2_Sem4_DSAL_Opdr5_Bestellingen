public class KandidaatRoute implements Comparable<KandidaatRoute> {
    private int score;
    private int [] route;

    public int getScore(){
        return this.score;
    }
    public void setScore(int score){
        this.score = score;
    }
    public int[] getRoute(){
        return this.route;
    }
    public void setRoute(int[] route){
        this.route = route;
    }

    @Override
    public int compareTo(KandidaatRoute o) {
        return Integer.compare(this.score, o.score);
    }
}
