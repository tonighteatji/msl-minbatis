package com.msl.minibatis.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * LRU缓存 最近最少使用
 * 在leetcode上写过这题
 *
 * @Author msl
 * @Date 2021-01-18 22:12
 */
public class LRUCache implements CacheFactory {

    /**
     * 缓存上限
     */
    private int capacity;
    private Map<Integer, ListNode> map;
    private ListNode head;
    private ListNode tail;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        map = new HashMap<>();
        head = new ListNode(-1, null);
        tail = head;
    }

    @Override
    public boolean containsKey(int key) {
        return map.containsKey(key);
    }

    @Override
    public Object get(int key) {
        if (!map.containsKey(key)) {
            return null;
        }
        // map中存放的是要找的节点的前驱
        ListNode pre = map.get(key);
        ListNode cur = pre.next;

        // 把当前节点删掉并移到尾部
        if (cur != tail) {
            pre.next = cur.next;
            // 更新它后面 node 的前驱
            map.put(cur.next.key, pre);
            map.put(cur.key, tail);
            moveToTail(cur);
        }
        return cur.val;
    }

    @Override
    public void put(int key, Object value) {
        if (get(key) != null) {
            map.get(key).next.val = value;
            return;
        }
        // 若不存在则 new 一个
        ListNode node = new ListNode(key, value);
        // 当前 node 的 pre 是 tail
        map.put(key, tail);
        moveToTail(node);

        if (map.size() > capacity) {
            map.remove(head.next.key);
            map.put(head.next.next.key, head);
            head.next = head.next.next;
        }
    }

    private void moveToTail(ListNode node) {
        node.next = null;
        tail.next = node;
        tail = tail.next;
    }


    private class ListNode {
        Integer key;
        Object val;
        ListNode next;

        public ListNode(Integer key, Object val) {
            this.key = key;
            this.val = val;
            this.next = null;
        }
    }

}
