package com.example.code_server.server.bridge.handlers;

import com.example.code_server.server.bridge.constants.JudgePriority;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

class PriorityMarker {
    int priority;

    PriorityMarker(int priority) {
        this.priority = priority;
    }
}


public class JudgeList {
    private final int priorities = 4;
    LinkedList<Object> queue = new LinkedList<>();
    List<PriorityMarker> priority = new ArrayList<>();
    JudgeHandler judge; //????
    HashMap<Integer, Object> nodeMap = new HashMap<>();
    ReentrantLock lock = new ReentrantLock();

    public JudgeList() {
        for (int i = 0; i < priorities; i++) {
            this.priority.add(new PriorityMarker(i));
        }
    }

    public void handleFreeJudge(){
        this.lock.lock();
        try {
            Object node = this.queue.getFirst();
            int priority = 0;
            while (node != null) {
                if (node instanceof PriorityMarker) {
                    priority = ((PriorityMarker) node).priority + 1;
                } else if (priority >= JudgePriority.REJUDGE_PRIORITY
                        && !judge.isWorking() && !judge.isDisabled())
                {
                    return;
                } else {
                    int id = ((Submission) node).getSubmissionId();
                    String problem = ((Submission) node).getProblemId();
                    String language = ((Submission) node).getLanguage();
                    String source = ((Submission) node).getSource();
                    String judgeId = ((Submission) node).getJudgeId();
                    if (this.judge.canJudge(problem, language, judgeId)) {
                        try {
                            this.judge.submit(id, problem, language, source);
                        } catch (Exception e) {
//                            System.out.println("Failed to dispatch " + id + " (" + problem + ", " + language + ") to " + judge.getName());
                            return;
                        }
//                        System.out.println("Dispatched queued submission " + id + ": " + judge.getName());
                        this.queue.remove(node);
                        this.nodeMap.remove(id);
                        break;
                    }
                }
                node = this.queue.getFirst();
            }
        } finally {
            this.lock.unlock();
        }

    }

    public void register() {
        lock.lock();
        try {
            this.disconnect(true);
            this.handleFreeJudge();
        } finally {
            lock.unlock();
        }
    }

    public void disconnect(boolean force) {
        this.lock.lock();
        try {
            this.judge.disconnect(force);
        } finally {
            this.lock.unlock();
        }
    }

    public void updateProblems() {
        this.lock.lock();
        try {
            this.handleFreeJudge();
        } finally {
            this.lock.unlock();
        }
    }

    public void updateDisableJudge(boolean isDisabled) {
        this.lock.lock();
        try {
            this.judge.setDisabled(isDisabled);
        } finally {
            this.lock.unlock();
        }
    }

    public void onJudgeFree() {
        System.out.println("Judge available after grading: " + judge.getName());
        this.lock.lock();
        try {
            this.judge.setWorking(false);
            this.handleFreeJudge();
        } finally {
            this.lock.unlock();
        }
    }

    public boolean abort(int submission) {
        System.out.println("Abort request: " + submission);
        this.lock.lock();
        try {
            try {
                this.queue.remove(this.nodeMap.get(submission));
                this.nodeMap.remove(submission);
            } catch (Exception e2) {
                return false;
            }
        } finally {
            this.lock.unlock();
        }
        return false;
    }

    public boolean checkPriority(int priority) {
        return 0 <= priority && priority < this.priorities;
    }

    public void judge(int id, String problem, String language, String source, String judgeId, int priority) {
        this.lock.lock();
        try {
            if (this.nodeMap.containsKey(id)) {
                return;
            }
            if (!judge.isWorking() && !judge.isDisabled()) {
                try {
                    this.judge.submit(id, problem, language, source);
                } catch (Exception e) {
                    System.out.println("Failed to dispatch " + id + " (" + problem + ", " + language + ") to " + judge.getName());
                }
            } else {
                this.nodeMap.put(id, this.queue.add(new Submission(id, problem, language, source, judgeId)), this.priority.get(priority));
                System.out.println("Queued submission: " + id);
            }
        } finally {
            this.lock.unlock();
        }
    }

//    def _handle_free_judge(self, judge):
//    with self.lock:
//    node = self.queue.first
//            priority = 0
//            while node:
//            if isinstance(node.value, PriorityMarker):
//    priority = node.value.priority + 1
//    elif priority >= REJUDGE_PRIORITY > 1 and
//            (not judge.working and not judge.is_disabled):
//            return
//            else:
//    id, problem, language, source, judge_id = node.value
//                    if judge.can_judge(problem, language, judge_id):
//                        try:
//                                judge.submit(id, problem, language, source)
//    except Exception:
//            logger.exception('Failed to dispatch %d (%s, %s) to %s', id, problem, language, judge.name)
//            return
//            logger.info('Dispatched queued submission %d: %s', id, judge.name)
//            self.queue.remove(node)
//    del self.node_map[id]
//            break
//    node = node.next
//
//    def register(self, judge):
//    with self.lock:
//            # Disconnect all judges with the same name, see <https://github.com/DMOJ/online-judge/issues/828>
//            self.disconnect(judge, force=True)
//            self._handle_free_judge(judge)
//
//    def disconnect(self, judge_id, force=False):
//    with self.lock:
//            judge.disconnect(force=force)
//
//    def update_problems(self, judge):
//    with self.lock:
//            self._handle_free_judge(judge)
//
//    def update_disable_judge(self, judge_id, is_disabled):
//    with self.lock:
//    judge.is_disabled = is_disabled
//
//
//    def on_judge_free(self, judge, submission):
//            logger.info('Judge available after grading %d: %s', submission, judge.name)
//    with self.lock:
//    judge._working = False
//            self._handle_free_judge(judge)
//
//    def abort(self, submission): -> Because only one judge can judge a submission, we can just remove the submission from the queue
//            logger.info('Abort request: %d', submission)
//    with self.lock:
//            try:
//    judge.abort()
//                return True
//    except KeyError:
//            try:
//    node = self.node_map[submission]
//    except KeyError:
//    pass
//                else:
//                        self.queue.remove(node)
//    del self.node_map[submission]
//            return False
//
//    def check_priority(self, priority):
//            return 0 <= priority < self.priorities
//
//    def judge(self, id, problem, language, source, judge_id, priority):
//    with self.lock:
//            if id in self.node_map:
//            # Already judging, don't queue again. This can happen during batch rejudges, rejudges should be
//            # idempotent.
//                return
//            if not judge.working and not judge.is_disabled:
//            # Schedule the submission on the judge reporting least load.
//            logger.info('Dispatched submission %d to: %s', id, judge.name)
//                try:
//                        judge.submit(id, problem, language, source)
//    except Exception:
//            else:
//    self.node_map[id] = self.queue.insert(
//            (id, problem, language, source, judge_id),
//    self.priority[priority],
//            )
//            logger.info('Queued submission: %d', id)
}
