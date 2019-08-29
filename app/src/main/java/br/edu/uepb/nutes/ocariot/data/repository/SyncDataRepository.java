package br.edu.uepb.nutes.ocariot.data.repository;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.edu.uepb.nutes.ocariot.data.model.ocariot.LogData;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.MultiStatusResult;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.PhysicalActivity;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Sleep;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Weight;
import br.edu.uepb.nutes.ocariot.data.repository.remote.fitbit.FitBitNetRepository;
import br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot.OcariotNetRepository;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SyncDataRepository {
    static SyncDataRepository mInstance;

    private FitBitNetRepository fitbitRepo;
    private OcariotNetRepository ocariotRepo;
    private String childId, startDate, endDate;

    private SyncDataRepository(Context context) {
        fitbitRepo = FitBitNetRepository.getInstance(context);
        ocariotRepo = OcariotNetRepository.getInstance(context);
        this.initConfig();
    }

    public static SyncDataRepository getInstance(Context context) {
        if (mInstance != null) return mInstance;

        mInstance = new SyncDataRepository(context);
        return mInstance;
    }

    private void initConfig() {
        this.startDate = DateUtils.getCurrentDate();
        this.endDate = DateUtils.addMonths(startDate, -6);
    }

    public Single<Object[]> syncAll(String childId) {
        if (childId == null || childId.isEmpty()) {
            throw new IllegalArgumentException("childId is required!");
        }

        this.childId = childId;

        List<Single<?>> requests = new ArrayList<>();
        requests.add(syncSteps());
        requests.add(syncCalories());
        requests.add(syncActiveMinutes());
        requests.add(syncLightlyActiveMinutes());
        requests.add(syncSedentaryMinutes());
        requests.add(syncPhysicalActivities());
        requests.addAll(getListSyncSleep());
        requests.addAll(getListSyncWeights());

        return Single.zip(requests, objects -> objects);
    }

    private Single<MultiStatusResult<LogData>> syncSteps() {
        return fitbitRepo.getSteps(startDate, endDate)
                .map(logDataList -> logDataList.toArray(new LogData[0]))
                .flatMap(steps -> ocariotRepo.publishSteps(childId, steps))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Single<MultiStatusResult<LogData>> syncCalories() {
        return fitbitRepo.getCalories(startDate, endDate)
                .map(logDataList -> logDataList.toArray(new LogData[0]))
                .flatMap(calories -> ocariotRepo.publishCalories(childId, calories))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Single<MultiStatusResult<LogData>> syncActiveMinutes() {
        return fitbitRepo.getActiveMinutes(startDate, endDate)
                .map(logDataList -> logDataList.toArray(new LogData[0]))
                .flatMap(calories -> ocariotRepo.publishActiveMinutes(childId, calories))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Single<MultiStatusResult<LogData>> syncLightlyActiveMinutes() {
        return fitbitRepo.getLightlyActiveMinutes(startDate, endDate)
                .map(logDataList -> logDataList.toArray(new LogData[0]))
                .flatMap(calories -> ocariotRepo.publishLightlyActiveMinutes(childId, calories))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Single<MultiStatusResult<LogData>> syncSedentaryMinutes() {
        return fitbitRepo.getSedentaryMinutes(startDate, endDate)
                .map(logDataList -> logDataList.toArray(new LogData[0]))
                .flatMap(calories -> ocariotRepo.publishSedentaryMinutes(childId, calories))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Single<MultiStatusResult<PhysicalActivity>> syncPhysicalActivities() {
        return fitbitRepo.listActivities(null, endDate, "asc", 0, 100)
                .map(activitiesList -> activitiesList.toArray(new PhysicalActivity[0]))
                .flatMap(activities -> ocariotRepo.publishPhysicalActivities(childId, activities))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private List<Single<?>> getListSyncSleep() {
        List<Single<?>> requests = new ArrayList<>();

        // startDate = today
        String currentStartDate = DateUtils.addMonths(startDate, -1);
        String currentEndDate = startDate;

        for (int i = 0; i < 6; i++) {
            requests.add(fitbitRepo.listSleep(currentStartDate, currentEndDate)
                    .map(sleepList -> sleepList.toArray(new Sleep[0]))
                    .flatMap(sleep -> {
                        Log.w("RES-SLEEP==", Arrays.toString(sleep));
                        return ocariotRepo.publishSleep(childId, sleep);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
            currentStartDate = DateUtils.addMonths(currentStartDate, -1);
            currentEndDate = DateUtils.addMonths(currentEndDate, -1);
        }
        return requests;
    }

    private List<Single<?>> getListSyncWeights() {
        List<Single<?>> requests = new ArrayList<>();

        String currentStartDate = DateUtils.addMonths(startDate, -1);
        String currentEndDate = startDate;

        for (int i = 0; i < 6; i++) {
            requests.add(fitbitRepo.listWeights(currentStartDate, currentEndDate)
                    .map(weightList -> weightList.toArray(new Weight[0]))
                    .flatMap(weights -> {
                        Log.w("RES-WEIGHT==", Arrays.toString(weights));
                        return ocariotRepo.publishWeights(childId, weights);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
            currentStartDate = DateUtils.addMonths(currentStartDate, -1);
            currentEndDate = DateUtils.addMonths(currentEndDate, -1);
        }
        return requests;
    }
}
