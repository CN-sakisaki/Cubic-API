import { Badge, Button, Card, Descriptions, Form, message, Spin, Table, Tabs, Tag } from 'antd';
import React, { useEffect, useState } from 'react';

import CodeHighlighting from '@/components/CodeHighlighting';
import { InterfaceRequestMethodEnum, statusEnum } from '@/enum/commonEnum';
import { errorCode } from '@/enum/ErrorCodeEnum';
import ApiTab from '@/pages/InterfaceInfo/components/ApiTab';
import {
  axiosExample,
  convertResponseParams,
  javaExample,
  returnExample,
} from '@/pages/InterfaceInfo/components/CodeTemplate';
import ToolsTab from '@/pages/InterfaceInfo/components/ToolsTab';
import { valueLength } from '@/pages/User/UserInfo';
import {
  getInterfaceInfoByIdUsingGet,
  invokeInterfaceInfoUsingPost,
} from '@/services/js-backend/interfaceInfoController';
import { history, Link, useModel, useParams } from '@@/exports';
import {
  BugOutlined,
  CodeOutlined,
  FileExclamationOutlined,
  FileTextOutlined,
  LoginOutlined,
  VerticalAlignBottomOutlined,
} from '@ant-design/icons';
import ProCard from '@ant-design/pro-card';
import Paragraph from 'antd/lib/typography/Paragraph';
import { stringify } from 'querystring';
import { Column } from 'rc-table';
import './index.less';

const Index: React.FC = () => {
  const { search, pathname } = window.location;
  const [loading, setLoading] = useState<boolean>(false);
  const [data, setDate] = useState<API.InterfaceInfo>();
  const [requestParams, setRequestParams] = useState<[]>();
  const [temporaryParams, setTemporaryParams] = useState<any>();
  const [responseParams, setResponseParams] = useState<[]>();
  const [requestExampleActiveTabKey, setRequestExampleActiveTabKey] = useState<string>('javadoc');
  const [activeTabKey, setActiveTabKey] = useState<
    'tools' | 'api' | 'errorCode' | 'sampleCode' | string
  >('api');
  const [result, setResult] = useState<string>();
  const [resultLoading, setResultLoading] = useState<boolean>(false);
  const params = useParams();
  const [form] = Form.useForm();
  const [axiosCode, setAxiosCode] = useState<any>();
  const [totalInvokes, setTotalInvokes] = useState<number>(0);
  const [javaCode, setJavaCode] = useState<any>();
  const [returnCode, setReturnCode] = useState<any>(returnExample);
  const docUrl =
    process.env.NODE_ENV === 'production' ? 'https://doc.qimuu.icu' : 'http://localhost:8080';
  const { initialState } = useModel('@@initialState');
  const { loginUser } = initialState || {};
  const loadedData = async () => {
    if (!params.id) {
      message.error('参数不存在');
      return;
    }
    setLoading(true);
    try {
      // @ts-ignore
      const res = await getInterfaceInfoByIdUsingGet({ id: params.id });
      if (res.data && res.code === 0) {
        setDate(res.data || {});
        setTotalInvokes(res.data.totalInvokes || 0);
        let requestParams = res.data.requestParams;
        let responseParams = res.data.responseParams;
        try {
          setRequestParams(requestParams ? JSON.parse(requestParams) : []);
          setResponseParams(responseParams ? JSON.parse(responseParams) : []);
        } catch (e: any) {
          setRequestParams([]);
          setResponseParams([]);
        }
        const response = res.data.responseParams
          ? JSON.parse(res.data.responseParams)
          : ([] as API.RequestParamsField);
        const convertedParams = convertResponseParams(response);
        setAxiosCode(axiosExample(res.data?.url, res.data?.method?.toLowerCase()));
        setJavaCode(javaExample(res.data?.url, res.data?.method?.toUpperCase()));
        setReturnCode(convertedParams);
      }
      setLoading(false);
    } catch (e: any) {
      message.error(e.message);
    }
  };
  useEffect(() => {
    loadedData();
  }, []);

  const requestExampleTabChange = (key: string) => {
    setRequestExampleActiveTabKey(key);
  };

  const responseExampleTabChange = (key: string) => {
    setActiveTabKey(key);
  };

  const responseExampleTabList = [
    {
      key: 'api',
      label: (
        <>
          <FileTextOutlined />
          API文档
        </>
      ),
    },
    {
      key: 'tools',
      label: (
        <>
          <BugOutlined />
          在线调试工具
        </>
      ),
    },
    {
      key: 'errorCode',
      label: (
        <>
          <FileExclamationOutlined />
          错误码参照
        </>
      ),
    },
    {
      key: 'sampleCode',
      label: (
        <>
          <CodeOutlined />
          示例代码
        </>
      ),
    },
  ];

  const onSearch = async (values: any) => {
    // 未登录跳转到登录页面
    if (!loginUser) {
      history.replace({
        pathname: '/user/login',
        search: stringify({
          redirect: pathname + search,
        }),
      });
    }

    setResultLoading(true);
    const res = await invokeInterfaceInfoUsingPost({
      id: data?.id,
      ...values,
    });
    if (res.code === 0) {
      setTotalInvokes(Number(totalInvokes) + 1);
    }
    setResult(JSON.stringify(res, null, 4));
    setResultLoading(false);
  };

  const responseExampleContentList: Record<string, React.ReactNode> = {
    api: (
      <ApiTab
        sampleCode={() => setActiveTabKey('sampleCode')}
        errorCodeTab={() => setActiveTabKey('errorCode')}
        requestParams={requestParams}
        responseParams={responseParams}
        returnCode={returnCode}
      />
    ),
    tools: (
      <ToolsTab
        form={form}
        data={data}
        temporaryParams={temporaryParams}
        onSearch={onSearch}
        requestExampleActiveTabKey={requestExampleActiveTabKey}
        paramsTableChange={(e: any) => {
          setTemporaryParams(e);
        }}
        result={result}
        resultLoading={resultLoading}
      />
    ),
    errorCode: (
      <>
        <p className="highlightLine">错误码：</p>
        <Table dataSource={errorCode} pagination={false} style={{ maxWidth: 800 }} size={'small'}>
          <Column title="参数名称" dataIndex="name" key="name" />
          <Column title="错误码" dataIndex="code" key="code" />
          <Column title="描述" dataIndex="des" key="des" />
        </Table>
      </>
    ),
    sampleCode: (
      <>
        <Tabs
          defaultActiveKey="javadoc"
          centered
          onChange={requestExampleTabChange}
          items={[
            {
              key: 'javadoc',
              label: 'java',
              children: <CodeHighlighting codeString={javaCode} language={'java'} />,
            },
            {
              key: 'javascript',
              label: 'axios',
              children: (
                <CodeHighlighting codeString={axiosCode} language={requestExampleActiveTabKey} />
              ),
            },
          ]}
        />
      </>
    ),
  };

  return (
    <Spin spinning={loading}>
      <Card title={data?.name}>
        <Descriptions>
          <Descriptions.Item key={'url'} label={'接口地址'}>
            <Paragraph copyable>{data?.url}</Paragraph>
          </Descriptions.Item>
          <Descriptions.Item key={'returnFormat'} label="返回格式">
            {data?.returnFormat ?? 'JSON'}
          </Descriptions.Item>
          <Descriptions.Item key={'reduceScore'} label="消费积分">
            {data?.reduceScore}个
          </Descriptions.Item>
          <Descriptions.Item key={'request'} label="请求方式">
            {' '}
            <Tag color={InterfaceRequestMethodEnum[data?.method ?? 'default']}>{data?.method}</Tag>
          </Descriptions.Item>
          <Descriptions.Item key={'totalInvokes'} label="调用总次数">
            {totalInvokes}次
          </Descriptions.Item>
          <Descriptions.Item key={'status'} label={'接口状态'}>
            {data && data.status === 0 ? (
              <Badge status="default" text={statusEnum[data.status]} />
            ) : null}
            {data && data.status === 1 ? (
              <Badge status="processing" text={statusEnum[data.status]} />
            ) : null}
            {data && data.status === 2 ? (
              <Badge status="error" text={statusEnum[data.status]} />
            ) : null}
          </Descriptions.Item>
          <Descriptions.Item key={'description'} label="接口描述">
            {data?.description ?? '该接口暂无描述信息'}
          </Descriptions.Item>
          <Descriptions.Item key={'请求示例'} label="请求示例">
            {data?.requestExample ? (
              <Paragraph copyable={valueLength(data?.requestExample)}>
                {data?.requestExample}
              </Paragraph>
            ) : (
              '该接口暂无请求示例'
            )}
          </Descriptions.Item>
        </Descriptions>
      </Card>
      <Card>
        <p className="highlightLine">接口详细描述请前往开发者在线文档查看：</p>
        <a href={`${docUrl}/pages/${data?.id}/#${data?.name}`} target={'_blank'} rel="noreferrer">
          📘 接口在线文档：{data?.name}
        </a>
      </Card>
      <br />
      <Card
        style={{ width: '100%' }}
        tabList={responseExampleTabList}
        activeTabKey={activeTabKey}
        onTabChange={responseExampleTabChange}
      >
        {responseExampleContentList[activeTabKey]}
      </Card>
      <br />
      {activeTabKey === 'sampleCode' && requestExampleActiveTabKey === 'javadoc' && (
        <ProCard
          type="inner"
          title={<strong>开发者 SDK（快速接入API接口）</strong>}
          bordered
          extra={
            <Link to="/account/center">
              <LoginOutlined /> 前往获取开发者凭证
            </Link>
          }
        >
          <Button size={'large'}>
            <a target={'_blank'} href={'https://github.com/qimu666/qi-api-sdk'} rel="noreferrer">
              <VerticalAlignBottomOutlined /> Java SDK
            </a>
          </Button>
        </ProCard>
      )}
    </Spin>
  );
};

export default Index;
