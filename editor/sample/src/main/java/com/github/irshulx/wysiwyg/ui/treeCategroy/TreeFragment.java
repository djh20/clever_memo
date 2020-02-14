package com.github.irshulx.wysiwyg.ui.treeCategroy;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;

import com.github.irshulx.wysiwyg.Database.DatabaseManager;
import com.github.irshulx.wysiwyg.R;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;


public class TreeFragment extends Fragment {

    private AndroidTreeView tView;
    DatabaseManager dbManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dbManager = new DatabaseManager(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_tree, container, false);
        ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.container);
        TreeNode root = TreeNode.root();
        TreeNode computerRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem("Root",true));
        TreeNode myDocuments = new TreeNode(new IconTreeItemHolder.IconTreeItem("공부",true));
        final TreeNode downloads = new TreeNode(new IconTreeItemHolder.IconTreeItem("C++",false));
        downloads.setClickListener(new TreeNode.TreeNodeClickListener(){
            @Override
            public void onClick(TreeNode node, Object value) {
                //pdf editor

            }
        });
        TreeNode file1 = new TreeNode(new IconTreeItemHolder.IconTreeItem("운영체제",false));
        TreeNode file2 = new TreeNode(new IconTreeItemHolder.IconTreeItem("네트워크",false));
        TreeNode file3 = new TreeNode(new IconTreeItemHolder.IconTreeItem("JAVA",false));
        TreeNode file4 = new TreeNode(new IconTreeItemHolder.IconTreeItem("Linux",false));
      //  fillDownloadsFolder(downloads);

        TreeNode myMedia = new TreeNode(new IconTreeItemHolder.IconTreeItem("일기",true));
        TreeNode photo1 = new TreeNode(new IconTreeItemHolder.IconTreeItem("취미",false));
        TreeNode photo2 = new TreeNode(new IconTreeItemHolder.IconTreeItem("여행",false));
        TreeNode photo3 = new TreeNode(new IconTreeItemHolder.IconTreeItem("드라마",false));
        myMedia.addChildren(photo1, photo2, photo3);

        myDocuments.addChildren(downloads,file1, file2, file3, file4);
        computerRoot.addChildren(myDocuments, myMedia);

        root.addChildren(computerRoot);

        tView = new AndroidTreeView(getActivity(), root);
        tView.setDefaultAnimation(true);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);

        containerView.addView(tView.getView());


        if (savedInstanceState != null) {
            String state = savedInstanceState.getString("tState");
            if (!TextUtils.isEmpty(state)) {
                tView.restoreState(state);
            }
        }
        return rootView;
    }

    private int counter = 0;
    private void fillDownloadsFolder(TreeNode node) {
        TreeNode downloads = new TreeNode(new IconTreeItemHolder.IconTreeItem("Downloads" + (counter++),false));
        node.addChild(downloads);
        if (counter < 5) {
            fillDownloadsFolder(downloads);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tState", tView.getSaveState());
    }
}

