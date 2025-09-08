import { Footer } from '@/components';
import { userRegisterUsingPost } from '@/services/js-backend/userController';
import { LockOutlined, MailOutlined, UserOutlined } from '@ant-design/icons';
import { LoginForm, ProFormCheckbox, ProFormText } from '@ant-design/pro-components';
import { Helmet, useLocation, useNavigate } from '@umijs/max';
import { Alert, message, Tabs, Typography } from 'antd';
import { createStyles } from 'antd-style';
import React, { useState } from 'react';
import Settings from '../../../../config/defaultSettings';

const { Link } = Typography;

const useStyles = createStyles(({ token }) => ({
  container: {
    display: 'flex',
    flexDirection: 'column',
    height: '100vh',
    overflow: 'auto',
    backgroundImage:
      "url('https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/V-_oS6r-i7wAAAAAAAAAAAAAFl94AQBr')",
    backgroundSize: '100% 100%',
  },
  footerLink: {
    textAlign: 'left', // 修改这里使文本左对齐
    marginTop: 16,
    marginBottom: 24, // 增加此行来为下方留出更多空间
  },
}));

const RegisterMessage: React.FC<{ content: string }> = ({ content }) => (
  <Alert style={{ marginBottom: 24 }} message={content} type="error" showIcon />
);

const Register: React.FC = () => {
  const [type, setType] = useState<string>('account');
  const [errorMessage, setErrorMessage] = useState<string>('');
  const { styles } = useStyles();
  const navigate = useNavigate();
  const location = useLocation();

  const handleSubmit = async (values: API.UserRegisterRequest) => {
    const { userPassword, checkPassword } = values;
    if (userPassword !== checkPassword) {
      message.error('两次输入的密码不一致');
      return;
    }
    try {
      const id = await userRegisterUsingPost(values);
      if (id) {
        message.success('注册成功，请登录！');
        navigate('/user/login', { state: { from: location } });
      }
    } catch (error: any) {
      const defaultRegisterFailureMessage = '注册失败，请重试！';
      console.error(error);
      setErrorMessage(error.message || defaultRegisterFailureMessage);
      message.error(error.message || defaultRegisterFailureMessage);
    }
  };

  return (
    <div className={styles.container}>
      <Helmet>
        <title>
          {'注册'}- {Settings.title}
        </title>
      </Helmet>
      <div style={{ flex: '1', padding: '32px 0' }}>
        <LoginForm
          contentStyle={{ minWidth: 280, maxWidth: '75vw' }}
          submitter={{ searchConfig: { submitText: '注册' } }}
          logo={
            <img
              alt="logo"
              src="/icons/icon_mo_fang.png"
              style={{ width: '60px', height: '60px' }}
            />
          }
          title="Cubic API"
          subTitle="Cubic API 是个人云接口平台"
          initialValues={{ autoLogin: true }}
          onFinish={async (values) => await handleSubmit(values as API.UserRegisterRequest)}
        >
          <Tabs activeKey={type} onChange={setType} centered>
            <Tabs.TabPane key="account" tab="账号密码注册" />
          </Tabs>

          {errorMessage && <RegisterMessage content={errorMessage} />}

          {type === 'account' && (
            <>
              <ProFormText
                name="userAccount"
                fieldProps={{ size: 'large', prefix: <UserOutlined /> }}
                placeholder="请输入账号"
                rules={[{ required: true, message: '账号是必填项！' }]}
              />
              <ProFormText
                name="email"
                fieldProps={{ size: 'large', prefix: <MailOutlined /> }}
                placeholder="请输入邮箱"
                rules={[{ type: 'email', message: '请输入有效的邮箱地址！' }]}
              />
              <ProFormText.Password
                name="userPassword"
                fieldProps={{ size: 'large', prefix: <LockOutlined /> }}
                placeholder="请输入密码"
                rules={[
                  { required: true, message: '密码是必填项！' },
                  { min: 8, message: '长度不能小于8' },
                ]}
              />
              <ProFormText.Password
                name="checkPassword"
                fieldProps={{ size: 'large', prefix: <LockOutlined /> }}
                placeholder="请再次输入密码"
                rules={[
                  { required: true, message: '确认密码是必填项！' },
                  { min: 8, message: '长度不能小于8' },
                ]}
              />
            </>
          )}
          <ProFormCheckbox
            initialValue={true}
            name="agreeToAnAgreement"
            rules={[
              () => ({
                validator(_, value) {
                  if (!value) {
                    return Promise.reject(new Error('同意协议后才可以登录'));
                  }
                  return Promise.resolve();
                },
                required: true,
              }),
            ]}
          >
            同意并接受《
            <a target={'_blank'} href={''} rel="noreferrer">
              隐私协议
            </a>
            》《
            <a target={'_blank'} href={''} rel="noreferrer">
              用户协议
            </a>
            》
          </ProFormCheckbox>

          <div className={styles.footerLink}>
            <Link onClick={() => navigate('/user/login')}>{'<< 我已有账号，去登录'}</Link>
          </div>
        </LoginForm>
      </div>
      <Footer />
    </div>
  );
};

export default Register;
