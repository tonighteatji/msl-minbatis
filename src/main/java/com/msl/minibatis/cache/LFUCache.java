package com.msl.minibatis.cache;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * LFU缓存 最少频率
 * 本来也想自己实现的，后来感觉嫌麻烦，直接去leetcode上捞一份题解过来改改用了
 *
 * @Author msl
 * @Date 2021-01-18 22:13
 */
public class LFUCache implements CacheFactory {

    /**
     * 存储缓存的内容
     */
    Map<Integer, Node> cache;

    /**
     * 存储每个频次对应的双向链表
     */
    Map<Integer, LinkedHashSet<Node>> freqMap;

    /**
     * 记录当前缓存数量
     */
    int size;

    /**
     * 缓存上限
     */
    int capacity;

    /**
     * 存储当前最小频次
     */
    int min;

    public LFUCache(int capacity) {
        cache = new HashMap<>(capacity);
        freqMap = new HashMap<>();
        this.capacity = capacity;
    }

    @Override
    public boolean containsKey(int key) {
        return cache.containsKey(key);
    }

    @Override
    public Object get(int key) {
        Node node = cache.get(key);
        if (node == null) {
            return -1;
        }
        freqInc(node);
        return node.value;
    }

    @Override
    public void put(int key, Object value) {
        if (capacity == 0) {
            return;
        }
        Node node = cache.get(key);
        if (node != null) {
            node.value = value;
            freqInc(node);
        } else {
            if (size == capacity) {
                Node deadNode = removeNode();
                cache.remove(deadNode.key);
                size--;
            }
            Node newNode = new Node(key, value);
            cache.put(key, newNode);
            addNode(newNode);
            size++;
        }
    }

    void freqInc(Node node) {
        // 从原freq对应的链表里移除, 并更新min
        int freq = node.freq;
        LinkedHashSet<Node> set = freqMap.get(freq);
        set.remove(node);
        if (freq == min && set.size() == 0) {
            min = freq + 1;
        }
        // 加入新freq对应的链表
        node.freq++;
        LinkedHashSet<Node> newSet = freqMap.get(freq + 1);
        if (newSet == null) {
            newSet = new LinkedHashSet<>();
            freqMap.put(freq + 1, newSet);
        }
        newSet.add(node);
    }

    void addNode(Node node) {
        LinkedHashSet<Node> set = freqMap.get(1);
        if (set == null) {
            set = new LinkedHashSet<>();
            freqMap.put(1, set);
        }
        set.add(node);
        min = 1;
    }

    Node removeNode() {
        LinkedHashSet<Node> set = freqMap.get(min);
        Node deadNode = set.iterator().next();
        set.remove(deadNode);
        return deadNode;
    }

    class Node {
        Integer key;
        Object value;
        int freq = 1;

        public Node() {
        }

        public Node(Integer key, Object value) {
            this.key = key;
            this.value = value;
        }
    }
}


