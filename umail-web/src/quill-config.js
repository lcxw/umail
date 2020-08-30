const toolBarOptions = [
  [{ font: [] }, { size: [] }],
  [{ color: [] }, { background: [] }],
  [{ header: 1 }, { header: 2 }],
  ["bold", "italic", "underline", "strike"],
  ["blockquote", "code-block"],
  [{ list: "ordered" }, { list: "bullet" }],
  [{ align: [] }],
  ["clean"],
  ["sourceEdit"] //HTML源代码编辑功能
];
const handlers = {
  shadeBox: null,
  sourceEdit: function() {
    const container = this.container;
    const firstChild = container.nextElementSibling.firstChild;
    if (!this.shadeBox) {
      let shadeBox = (this.shadeBox = document.createElement("div"));
      shadeBox.style.cssText =
        "position:absolute; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.5); cursor:pointer";
      container.style.position = "relative";
      shadeBox.addEventListener(
        "click",
        function() {
          this.style.display = "none";
          firstChild.innerHTML = firstChild.innerText.trim();
        },
        false
      );
      container.appendChild(shadeBox);
      firstChild.innerText = firstChild.innerHTML;
    } else {
      this.shadeBox.style.display = "block";
      firstChild.innerText = firstChild.innerHTML;
    }
  }
};

export default {
  placeholder: "请输入内容",
  theme: "snow", // 主题
  modules: {
    toolbar: {
      container: toolBarOptions, // 工具栏选项
      handlers: handlers // 事件重写
    }
  },
  initButton: function() {
    // 初始化按钮样式
    document.querySelector(".ql-toolbar").style.cssText = "text-align:left;";
    document.querySelector(".ql-font").title = "字体";
    document.querySelector(".ql-size").title = "字体大小";
    document.querySelector(".ql-color").title = "字体颜色";
    document.querySelector(".ql-background").title = "背景颜色";
    document.querySelector(".ql-header").title = "标题";
    document.querySelector(".ql-bold").title = "加粗";
    document.querySelector(".ql-italic").title = "斜体";
    document.querySelector(".ql-underline").title = "下划线";
    document.querySelector(".ql-strike").title = "删除";
    document.querySelector(".ql-code-block").title = "代码";
    document.querySelector(".ql-list").title = "列表";
    document.querySelector(".ql-align").title = "对齐";
    document.querySelector(".ql-clean").title = "清除格式";
    const sourceEditorButton = document.querySelector(".ql-sourceEdit");
    sourceEditorButton.classList.add("iconfont");
    sourceEditorButton.classList.add("umail-bianji");
    sourceEditorButton.style.cssText = "font-size:1.2em";
    sourceEditorButton.title = "源码编辑";
  }
};
