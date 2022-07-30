package com.langong.emcservice.util;

import java.util.*;

public class TreeUtil {

    /**
     * 构建树节点
     */
    public static <T extends CategoryTreeNode> List<T> build(List<T> treeNodes) {

        return getChildPerms(treeNodes, null);
    }

    public static  <T extends CategoryTreeNode> List<T> getChildPerms(List<T> list, String parentId) {
        List<T> returnList = new ArrayList<>();
        for (T t : list) {
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId()==null || t.getParentId().equals("") || t.getParentId().equals(parentId)) {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     *
     * @param list 数组列表
     * @param t   当前节点
     */
    private static  <T extends CategoryTreeNode> void recursionFn(List<T> list, T t) {
        // 得到子节点列表
        List<T> childList = getChildList(list, t);
        t.setChildren(childList);
        for (T tChild : childList) {
            if (hasChild(list, tChild)) {
                // 判断是否有子节点
                for (T n : childList) {
                    recursionFn(list, n);
                }
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private static  <T extends CategoryTreeNode> List<T> getChildList(List<T> list, T t) {
        List<T> tlist = new ArrayList<>();
        for (T n : list) {
            if (n.getParentId() != null && n.getParentId().equals(t.getId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private static  <T extends CategoryTreeNode> boolean hasChild(List<T> list, T t) {
        return getChildList(list, t).size() > 0;
    }

}