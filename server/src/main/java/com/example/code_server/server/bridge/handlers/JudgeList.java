package com.example.code_server.server.bridge.handlers;

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
    private static final int priorities = 4;
    LinkedList<Object> queue = new LinkedList<>();
    List<PriorityMarker> priority = new ArrayList<>();
    JudgeHandler judge;
    HashMap<Integer, Object> nodeMap = new HashMap<>();
    ReentrantLock lock = new ReentrantLock();

    public JudgeList() {
        for (int i = 0; i < priorities; i++) {
            priority.add(new PriorityMarker(i));
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
