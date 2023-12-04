import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class RouteCalc {
    private int EPOCHS;
    private int KANDIDATEN;
    final int TOTALDEST = 250;

    private int[] destinations;
    private int[] packages;
    private int[][] distances;
    private int epochTeller = 0;

    private ArrayList<KandidaatRoute> huidigeKandidaten = new ArrayList<>();

    public RouteCalc(int epochs, int kandidaten) {
        this.EPOCHS = epochs;
        this.KANDIDATEN = kandidaten;
    }

    public void readSituation(String file) {
        File situationfile = new File(file);
        Scanner scan = null;
        try {
            scan = new Scanner(situationfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int size = scan.nextInt();
        destinations = new int[size];
        packages = new int[size];
        distances = new int[TOTALDEST][TOTALDEST];

        for (int i = 0; i < size; i++) {
            destinations[i] = scan.nextInt();
        }
        for (int i = 0; i < size; i++) {
            packages[i] = scan.nextInt();
        }
        for (int i = 0; i < TOTALDEST; i++) {
            for (int j = 0; j < TOTALDEST; j++) {
                distances[i][j] = scan.nextInt();
            }
        }
    }

    public void bepaalRoute() {
        if (epochTeller < 1) {
            startSituatie();
        }
        for (int i = 0; i < this.EPOCHS; i++) {
            evalueerEpoch();
            volgendeEpoch();
        }
        evalueerEpoch();
        Collections.sort(this.huidigeKandidaten);
        System.out.println(Arrays.toString(this.huidigeKandidaten.get(0).getRoute()));
        System.out.println(this.huidigeKandidaten.get(0).getScore());
    }

    public void evalueerKandidaat(KandidaatRoute kandidaatRoute) {
        int totalDistance = 0;
        int totalPackages = 0;
        int totalDistanceScore;
        int totalPackagesScore;
        int totalStartScore = 0;
        int[] currentRoute = kandidaatRoute.getRoute();

        //calculating the total distance
        for (int i = 0; i < currentRoute.length; i++) {
            try {
                totalDistance += distances[currentRoute[i]][currentRoute[i + 1]];
            } catch (ArrayIndexOutOfBoundsException a) {
            }
        }
        //set the total distance score
        totalDistanceScore = totalDistance;

        //get total packages
        for (int i = 0; i < this.packages.length; i++) {
            totalPackages += this.packages[i];
        }


        //calculate totalpackages score
        ArrayList<Integer> copyPackages = new ArrayList<>();
        //fill arraylist with packages
        for (int aPackage : this.packages) {
            copyPackages.add(aPackage);
        }
        //packages hold after every destination
        for (int i = 0; i < this.packages.length; i++) {
            for (int x = 0; x < copyPackages.size(); x++) {
                try {
                    totalPackages += copyPackages.get(x + i);
                } catch (IndexOutOfBoundsException e) {
                }
            }
        }
        totalPackagesScore = totalPackages / 10;
        //calculate startRoute
        if (kandidaatRoute.getRoute()[0] != 1) {
            totalStartScore += 100000;
        }
        kandidaatRoute.setScore(totalDistanceScore + totalPackagesScore + totalStartScore);
    }

    public void evalueerEpoch() {
        for (KandidaatRoute i : huidigeKandidaten) {
            if (i.getScore() == 0) {
                evalueerKandidaat(i);
            }
        }
    }

    public KandidaatRoute randomKandidaat() {
        int[] randomRoute = new int[this.destinations.length];
        KandidaatRoute kandidaatRoute = new KandidaatRoute();
        ArrayList<Integer> copyDestinations = new ArrayList();
        Random random = new Random();

        //fill arraylist copyDestinations;
        for (int i : this.destinations) {
            copyDestinations.add(i);
        }

        //get random destination by getting at the index from destinationCopy and removing it after.
        for (int i = 0; i < randomRoute.length; i++) {
            int randomGetal = random.nextInt(copyDestinations.size());
            randomRoute[i] = copyDestinations.get(randomGetal);
            copyDestinations.remove(randomGetal);
        }
        kandidaatRoute.setRoute(randomRoute);
        return kandidaatRoute;
    }

    public void startSituatie() {
        for (int i = 0; i < this.KANDIDATEN; i++) {
            this.huidigeKandidaten.add(randomKandidaat());
        }

    }

    public KandidaatRoute muteer(KandidaatRoute kandidaatRoute) {
        KandidaatRoute mutation = new KandidaatRoute();
        int[] routeArray = new int[kandidaatRoute.getRoute().length];
        //fill the array
        for (int x = 0; x < kandidaatRoute.getRoute().length; x++) {
            int y = kandidaatRoute.getRoute()[x];
            routeArray[x] = y;
        }
        Random random = new Random();
        for (int i = routeArray.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            //Swap
            int x = routeArray[index];
            routeArray[index] = routeArray[i];
            routeArray[i] = x;
        }
        mutation.setRoute(routeArray);
        return mutation;
    }

    public int getPercentKandidaten(int percentage) {
        float p = (float) percentage / 100;
        float x = (int) Math.ceil(this.KANDIDATEN * p);
        return (int) x;
    }


    public void volgendeEpoch() {
        Collections.sort(this.huidigeKandidaten);
        ArrayList<KandidaatRoute> besteOplossingen = new ArrayList<>();
        ArrayList<KandidaatRoute> besteOplossingenMutated = new ArrayList<>();
        ArrayList<KandidaatRoute> oplossingenRandom = new ArrayList<>();
        //45% best solutions to list
        for (int i = 0; i < getPercentKandidaten(45); i++) {
            besteOplossingen.add(huidigeKandidaten.get(i));
            besteOplossingenMutated.add(muteer(huidigeKandidaten.get(i)));
        }

        //new 10%
        for (int i = 0; i < getPercentKandidaten(10); i++) {
            oplossingenRandom.add(randomKandidaat());
        }
        this.huidigeKandidaten.clear();

        //add to epoch list
        for (KandidaatRoute i : besteOplossingen) {
            this.huidigeKandidaten.add(i);
        }
        for (KandidaatRoute i : besteOplossingenMutated) {
            this.huidigeKandidaten.add(i);
        }
        for (KandidaatRoute i : oplossingenRandom) {
            this.huidigeKandidaten.add(i);
        }
        besteOplossingen.clear();
        besteOplossingenMutated.clear();
        oplossingenRandom.clear();
        for (KandidaatRoute i : huidigeKandidaten) {
            System.out.println("test: " + i.getScore());
        }
        System.out.println("-------einde van volgende epoch --------");

        epochTeller++;
    }


}
