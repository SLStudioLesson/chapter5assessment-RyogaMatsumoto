package com.taskapp.logic;

import java.time.LocalDate;
import java.util.List;

import com.taskapp.dataaccess.LogDataAccess;
import com.taskapp.dataaccess.TaskDataAccess;
import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.exception.AppException;
import com.taskapp.model.Log;
import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskLogic {
    private final TaskDataAccess taskDataAccess;
    private final LogDataAccess logDataAccess;
    private final UserDataAccess userDataAccess;


    public TaskLogic() {
        taskDataAccess = new TaskDataAccess();
        logDataAccess = new LogDataAccess();
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param taskDataAccess
     * @param logDataAccess
     * @param userDataAccess
     */
    public TaskLogic(TaskDataAccess taskDataAccess, LogDataAccess logDataAccess, UserDataAccess userDataAccess) {
        this.taskDataAccess = taskDataAccess;
        this.logDataAccess = logDataAccess;
        this.userDataAccess = userDataAccess;
    }

    /**
     * 全てのタスクを表示します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findAll()
     * @param loginUser ログインユーザー
     */
    public void showAll(User loginUser) {
        List<Task> tasks = taskDataAccess.findAll();

        tasks.forEach(t -> {
            String tantou = "あなたが担当しています";
            // ログインしているユーザーとタスクの担当者が違った場合担当者名を出力
            User repUser = t.getRepUser();
            if (loginUser.getCode() != repUser.getCode()) {
                tantou = repUser.getName() + "が担当しています";
            }

            // タスクステータス
            String statu = "未着手";
            if (t.getStatus() == 1) {
                statu = "着手中";
            } else if (t.getStatus() == 2) {
                statu = "完了";
            }

            System.out.println(t.getCode() + ". タスク名：" + t.getName() + ", 担当者名：" + tantou + ", ステータス：" + statu);
        });
    }

    /**
     * 新しいタスクを保存します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#save(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param name タスク名
     * @param repUserCode 担当ユーザーコード
     * @param loginUser ログインユーザー
     * @throws AppException ユーザーコードが存在しない場合にスローされます
     */
    public void save(int code, String name, int repUserCode, User loginUser) throws AppException {
        int status = 0;
        // repUserCode に入力されたコードと同じコードのユーザー情報を取得
        User repUser = userDataAccess.findByCode(repUserCode);

        if (repUser == null) {
            throw new AppException("存在するユーザーコードを入力してください");
        }

        Task task = new Task(code, name, status, repUser);
        taskDataAccess.save(task);

        // タスクを作成したユーザーのコードを入れる
        int changeUserCode = loginUser.getCode();
        Log log = new Log(code, changeUserCode, status, LocalDate.now());
        logDataAccess.save(log);
        System.out.println(name + "の登録が完了しました。");
    }

    /**
     * タスクのステータスを変更します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#update(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param status 新しいステータス
     * @param loginUser ログインユーザー
     * @throws AppException タスクコードが存在しない、またはステータスが前のステータスより1つ先でない場合にスローされます
     */
    public void changeStatus(int code, int status, User loginUser) throws AppException {
        // 選択したタスクのデータを取得する
        Task task = taskDataAccess.findByCode(code);

        // 既に登録されているタスクか判定する
        if (task == null) {
            throw new AppException("存在するタスクコードを入力してください");
        }

        // 入力されたステータスが選択したタスクのステータスの1つ先か判定する
        if (task.getStatus() + 1 != status) {
            throw new AppException("ステータスは、前のステータスより1つ先のもののみを選択してください");
        }

        // ステータスのみ更新
    }

    /**
     * タスクを削除します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#delete(int)
     * @see com.taskapp.dataaccess.LogDataAccess#deleteByTaskCode(int)
     * @param code タスクコード
     * @throws AppException タスクコードが存在しない、またはタスクのステータスが完了でない場合にスローされます
     */
    // public void delete(int code) throws AppException {
    // }
}